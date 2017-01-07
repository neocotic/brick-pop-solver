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
package com.neocotic.brickpopsolver.solution.serial;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.Board;
import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.Move;
import com.neocotic.brickpopsolver.service.AbstractService;
import com.neocotic.brickpopsolver.solution.Solution;
import com.neocotic.brickpopsolver.solution.SolutionException;
import com.neocotic.brickpopsolver.solution.SolutionSearch;
import com.neocotic.brickpopsolver.solution.SolutionService;

public class SerialSolutionService extends AbstractService implements SolutionService {

    private static final Logger LOG = LogManager.getLogger(SerialSolutionService.class);

    public static final String SERVICE_NAME = "serial";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public Solution solve(final Board board, final Configuration configuration) throws SolutionException {
        LOG.traceEntry("solve(board={}, configuration={})", board, configuration);

        LOG.debug("Attempting serial solve for board:{}{}", SystemUtils.LINE_SEPARATOR, board);

        for (final Move move : board.getAvailableMoves()) {
            try {
                final Solution solution = new SolutionSearch(configuration, move).search();

                LOG.debug("Found solution:{}{}", SystemUtils.LINE_SEPARATOR, solution);

                return LOG.traceExit(solution);
            } catch (SolutionException e) {
                // Ignore failed solution
            }
        }

        LOG.debug("No solution found");

        return LOG.traceExit(new Solution(configuration));
    }
}
