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
package com.neocotic.brickpopsolver.solution.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.Board;
import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.Move;
import com.neocotic.brickpopsolver.service.AbstractService;
import com.neocotic.brickpopsolver.solution.Solution;
import com.neocotic.brickpopsolver.solution.SolutionException;
import com.neocotic.brickpopsolver.solution.SolutionSearch;
import com.neocotic.brickpopsolver.solution.SolutionService;

public final class SerialSolutionService extends AbstractService implements SolutionService {

    private static final Logger logger = LoggerFactory.getLogger(SerialSolutionService.class);

    public static final String SERVICE_NAME = "serial";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public Solution solve(final Board board, final Configuration configuration) throws SolutionException {
        logger.trace("solve:enter(board={}, configuration={})", board, configuration);

        logger.debug("Attempting to solve board:{}{}", System.lineSeparator(), board);

        for (final Move move : board.getAvailableMoves()) {
            try {
                final Solution solution = new SolutionSearch(configuration, move).search();

                logger.debug("Found solution:{}{}", System.lineSeparator(), solution);

                logger.trace("solve:exit({})", solution);
                return solution;
            } catch (SolutionException e) {
                // Ignore failed solution
            }
        }

        logger.warn("No solution found");

        final Solution solution = new Solution(configuration);

        logger.trace("solve:exit({})", solution);
        return solution;
    }
}
