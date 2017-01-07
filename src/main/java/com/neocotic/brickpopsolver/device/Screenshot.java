/*
 * Copyright (C) 2017 Alasdair Mercer
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
package com.neocotic.brickpopsolver.device;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.Board;
import com.neocotic.brickpopsolver.Color;
import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.Coordinate;
import com.neocotic.brickpopsolver.CustomToStringStyle;
import com.neocotic.brickpopsolver.image.Image;

public final class Screenshot {

    private static final Logger LOG = LogManager.getLogger(Screenshot.class);

    private final Path filePath;
    private final Image image;

    public Screenshot(final Path filePath, final Image image) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
        this.image = Objects.requireNonNull(image, "image");
    }

    public Board createBoard(final Configuration configuration) {
        LOG.traceEntry("createBoard(configuration={})", configuration);

        final Map<Coordinate, Color> map = new LinkedHashMap<>();
        final int offset = configuration.getOffset();
        final Coordinate start = configuration.getStart();

        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                final int x = start.getRow() + (i * offset);
                final int y = start.getColumn() + (j * offset);

                map.put(new Coordinate(i, j), image.getPixel(x, y));
            }
        }

        return LOG.traceExit(new Board(map));
    }

    public Path getFilePath() {
        return filePath;
    }

    public Image getImage() {
        return image;
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

        final Screenshot other = (Screenshot) obj;
        return new EqualsBuilder()
            .append(filePath, other.filePath)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(filePath)
            .hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, CustomToStringStyle.SHORT_STYLE)
            .append("filePath", filePath)
            .toString();
    }
}
