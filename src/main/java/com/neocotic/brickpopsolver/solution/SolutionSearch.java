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
package com.neocotic.brickpopsolver.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.Coordinate;
import com.neocotic.brickpopsolver.Move;

public final class SolutionSearch implements Callable<Solution> {

    private static final Logger LOG = LogManager.getLogger(SolutionSearch.class);

    private final Configuration configuration;
    private final Move move;
    private Solution solution;

    public SolutionSearch(final Configuration configuration, final Move move) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.move = Objects.requireNonNull(move, "move");
    }

    @Override
    public Solution call() throws Exception {
        return search();
    }

    public Solution search() throws SolutionException {
        LOG.traceEntry();

        search(Collections.singletonList(move), Collections.emptyList());

        if (solution == null) {
            throw new SolutionException("Could not solve move: " + move);
        }

        return LOG.traceExit(solution);
    }

    private void search(final List<Move> moves, final List<Coordinate> steps) {
        for (final Move move : moves) {
            if (solution != null) {
                break;
            }

            final List<Coordinate> solutionSteps = new ArrayList<>(steps);
            solutionSteps.add(move.getCoordinate());

            if (move.getBoard().isSolved()) {
                solution = new Solution(configuration, solutionSteps);
            } else {
                search(move.getBoard().getAvailableMoves(), solutionSteps);
            }
        }
    }
}
