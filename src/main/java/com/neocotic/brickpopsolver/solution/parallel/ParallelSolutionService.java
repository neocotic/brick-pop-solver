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
package com.neocotic.brickpopsolver.solution.parallel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.Board;
import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.service.AbstractService;
import com.neocotic.brickpopsolver.solution.Solution;
import com.neocotic.brickpopsolver.solution.SolutionException;
import com.neocotic.brickpopsolver.solution.SolutionSearch;
import com.neocotic.brickpopsolver.solution.SolutionService;

public final class ParallelSolutionService extends AbstractService implements SolutionService {

    private static final Logger logger = LoggerFactory.getLogger(ParallelSolutionService.class);

    public static final String SERVICE_NAME = "parallel";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public Solution solve(final Board board, final Configuration configuration) throws SolutionException {
        logger.trace("solve:enter(board={}, configuration={})", board, configuration);

        logger.debug("Attempting to solve board:{}{}", System.lineSeparator(), board);

        final ExecutorService executor = Executors.newWorkStealingPool();
        final List<SolutionSearch> searches = board.getAvailableMoves().stream()
            .map(move -> new SolutionSearch(configuration, move))
            .collect(Collectors.toList());
        Solution solution;

        try {
            solution = executor.invokeAny(searches);

            logger.debug("Found solution:{}{}", System.lineSeparator(), solution);
        } catch (ExecutionException e) {
            logger.warn("No solution found", e);

            solution = new Solution(configuration);
        } catch (InterruptedException e) {
            throw new SolutionException("Solution failed", e);
        }

        logger.trace("solve:exit({})", solution);
        return solution;
    }
}
