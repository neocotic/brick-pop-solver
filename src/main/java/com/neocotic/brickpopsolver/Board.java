/*
 * Copyright (C) 2018 Alasdair Mercer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.neocotic.brickpopsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.device.Point;
import com.neocotic.brickpopsolver.device.Screenshot;

public final class Board {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private static final int GRID_SIZE = 10;
    private static final Coordinate[] NEIGHBORS = {new Coordinate(-1, 0), new Coordinate(0, 1), new Coordinate(1, 0), new Coordinate(0, -1)};

    public static Board fromScreenshot(final Screenshot screenshot, final Configuration configuration) {
        logger.trace("fromScreenshot:enter(screenshot={}, configuration={})", screenshot, configuration);

        final Map<Coordinate, Color> map = new LinkedHashMap<>();
        final int offset = configuration.getOffset();
        final Point start = configuration.getStart();

        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                final int x = start.getX() + (j * offset);
                final int y = start.getY() + (i * offset);

                map.put(new Coordinate(i, j), screenshot.getImage().getPixel(x, y));
            }
        }

        final Board board = new Board(map);

        logger.trace("fromScreenshot:exit({})", board);
        return board;
    }

    private final Color[][] grid;

    public Board(final Color[][] grid) {
        this.grid = Objects.requireNonNull(grid, "grid");
    }

    public Board(final Map<Coordinate, Color> map) {
        Objects.requireNonNull(map, "map");

        final Set<Coordinate> coordinates = map.keySet();
        final int maxColumn = coordinates.stream()
            .map(Coordinate::getColumn)
            .max(Comparator.naturalOrder())
            .orElse(0) + 1;
        final int maxRow = coordinates.stream()
            .map(Coordinate::getRow)
            .max(Comparator.naturalOrder())
            .orElse(0) + 1;

        final Color[][] grid = new Color[maxRow][];
        for (int i = 0; i < maxRow; i++) {
            final Color[] colors = new Color[maxColumn];
            for (int j = 0; j < maxColumn; j++) {
                colors[j] = map.get(new Coordinate(i, j));
            }

            grid[i] = colors;
        }

        this.grid = grid;
    }

    public List<Move> getAvailableMoves() {
        logger.trace("getAvailableMoves:enter()");

        final List<Move> moves = new ArrayList<>();
        final Set<Board> pools = new HashSet<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                final Color color = grid[i][j];

                if (!color.isEmpty()) {
                    final Coordinate coordinate = new Coordinate(i, j);
                    final Board board = popFrom(coordinate);

                    if (board != null && !pools.contains(board)) {
                        pools.add(board);
                        moves.add(new Move(coordinate, board));
                    }
                }
            }
        }

        logger.trace("getAvailableMoves:exit({})", moves);
        return moves;
    }

    public Color getColor(final Coordinate coordinate) {
        return grid[coordinate.getRow()][coordinate.getColumn()];
    }

    public boolean isSolved() {
        return grid[0].length == 0;
    }

    private Board contract() {
        final Map<Integer, List<Integer>> emptyIndices = new HashMap<>();

        for (int j = 0; j < grid[0].length; j++) {
            final Color[] colors = extractColumn(j);
            final List<Integer> indices = new ArrayList<>();

            for (int index = 0; index < colors.length; index++) {
                if (colors[index].isEmpty()) {
                    indices.add(index);
                }
            }

            emptyIndices.put(j, indices);
        }

        final Color emptyColor = new Color();
        final List<List<Color>> shiftedColumns = new ArrayList<>();

        for (int j = 0; j < grid[0].length; j++) {
            final List<Color> colors = new ArrayList<>();
            final int emptyCount = emptyIndices.get(j).size();

            for (int index = 0; index < emptyCount; index++) {
                colors.add(emptyColor);
            }

            colors.addAll(Arrays.stream(extractColumn(j))
                .filter(color -> !color.isEmpty())
                .collect(Collectors.toList()));

            shiftedColumns.add(colors);
        }

        final List<List<Color>> contractedColumns = shiftedColumns.stream()
            .filter(colors -> colors.stream().anyMatch(color -> !color.isEmpty()))
            .collect(Collectors.toList());
        final int contractedColumnCount = contractedColumns.size();
        final Color[][] contractedGrid = new Color[grid.length][];

        for (int i = 0; i < grid.length; i++) {
            final Color[] colors = new Color[contractedColumnCount];
            for (int j = 0; j < contractedColumnCount; j++) {
                colors[j] = contractedColumns.get(j).get(i);
            }

            contractedGrid[i] = colors;
        }

        return new Board(contractedGrid);
    }

    private Color[] extractColumn(final int column) {
        final Color[] columnColors = new Color[grid.length];
        for (int i = 0; i < grid.length; i++) {
            columnColors[i] = grid[i][column];
        }

        return columnColors;
    }

    private Set<Coordinate> floodIndices(final Coordinate coordinate) {
        final Set<Coordinate> flood = new LinkedHashSet<>();
        final Color floodColor = getColor(coordinate);
        final Deque<Coordinate> queue = new LinkedList<>();
        queue.add(coordinate);

        while (!queue.isEmpty()) {
            final Coordinate location = queue.pop();
            flood.add(location);

            queue.addAll(getNeighbors(location).stream()
                .filter(neighbor -> floodColor.equals(getColor(neighbor)) && !flood.contains(neighbor))
                .collect(Collectors.toList()));
        }

        return flood;
    }

    private List<Coordinate> getNeighbors(final Coordinate coordinate) {
        return Arrays.stream(NEIGHBORS)
            .map(neighbor -> coordinate.offset(neighbor.getRow(), neighbor.getColumn()))
            .filter(this::isCoordinateValid)
            .collect(Collectors.toList());
    }

    private boolean isCoordinateValid(final Coordinate coordinate) {
        final int column = coordinate.getColumn();
        final int row = coordinate.getRow();

        return (row >= 0 && row < grid.length) && (column >= 0 && column < grid[0].length);
    }

    private Board popFrom(final Coordinate coordinate) {
        final Set<Coordinate> flood = floodIndices(coordinate);
        // Flood pool must contain multiple elements to be popped
        if (flood.size() == 1) {
            return null;
        }

        final Color[][] updatedGrid = new Color[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            final Color[] colors = new Color[grid[i].length];
            for (int j = 0; j < grid[i].length; j++) {
                colors[j] = flood.contains(new Coordinate(i, j)) ? Color.EMPTY : grid[i][j];
            }

            updatedGrid[i] = colors;
        }

        return new Board(updatedGrid).contract();
    }

    public Color[][] getGrid() {
        return grid;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        final Board other = (Board) obj;
        return new EqualsBuilder()
            .append(grid, other.grid)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(grid)
            .hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder gridBuilder = new StringBuilder(SystemUtils.LINE_SEPARATOR);
        for (final Color[] colors : grid) {
            gridBuilder.append("  ");
            gridBuilder.append(StringUtils.join(colors, ','));
            gridBuilder.append(SystemUtils.LINE_SEPARATOR);
        }

        return new ToStringBuilder(this, CustomToStringStyle.SHORT_STYLE)
            .append("grid", gridBuilder)
            .toString();
    }
}
