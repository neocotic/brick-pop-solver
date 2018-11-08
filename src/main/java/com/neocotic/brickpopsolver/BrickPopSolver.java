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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.device.DeviceService;
import com.neocotic.brickpopsolver.device.Screenshot;
import com.neocotic.brickpopsolver.image.ImageService;
import com.neocotic.brickpopsolver.service.ServiceNotFoundException;
import com.neocotic.brickpopsolver.solution.Solution;
import com.neocotic.brickpopsolver.solution.SolutionException;
import com.neocotic.brickpopsolver.solution.SolutionService;

public final class BrickPopSolver {

    private static final Logger logger = LoggerFactory.getLogger(BrickPopSolver.class);

    private static final String PROPERTY_PREFIX = "brickpopsolver.";

    public static void main(final String[] args) throws Exception {
        logger.trace("main:enter(args={})", new Object[]{args});

        final BrickPopSolver solver = new BrickPopSolver();
        if (ArrayUtils.isEmpty(args)) {
            solver.solve().play();
        } else {
            solver.solve(Paths.get(args[0]));
        }

        logger.trace("main:exit()");
    }

    private static Integer getIntegerProperty(final String key) {
        final String value = getProperty(key);
        return value != null ? Integer.valueOf(value) : null;
    }

    private static String getProperty(final String key) {
        return System.getProperty(PROPERTY_PREFIX + key);
    }

    private final Configuration configuration;

    public BrickPopSolver() throws ServiceNotFoundException {
        final String deviceServiceName = getProperty("deviceService.name");
        final String imageServiceName = getProperty("imageService.name");
        final String solutionServiceName = getProperty("solutionService.name");
        final String imageFormatName = getProperty("imageFormat.name");
        final Integer offset = getIntegerProperty("offset");
        final Integer startX = getIntegerProperty("start.x");
        final Integer startY = getIntegerProperty("start.y");

        configuration = new Configuration(deviceServiceName, imageServiceName, solutionServiceName, imageFormatName, offset, startX, startY);
    }

    public BrickPopSolver(final Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");

        logger.debug("BrickPopSolver created with configuration:{}{}", System.lineSeparator(), configuration);
    }

    public Solution solve() throws BrickPopSolverException {
        logger.trace("solve:enter()");

        final DeviceService deviceService = configuration.getDeviceService();
        final Set<String> devices = deviceService.getDevices(configuration);

        if (logger.isInfoEnabled()) {
            logger.info("{} connected devices found", devices.size());
        }

        final Screenshot screenshot = deviceService.captureScreenshot(createScreenshotFile(), configuration);

        logger.info("Captured screenshot from device: {}", screenshot);

        final Solution solution = solve(screenshot);

        logger.trace("solve:exit({})", solution);
        return solution;
    }

    public Solution solve(final Path filePath) throws BrickPopSolverException {
        logger.trace("solve:enter(filePath={})", filePath);

        Objects.requireNonNull(filePath, "filePath");

        final ImageService imageService = configuration.getImageService();
        final Screenshot screenshot = new Screenshot(filePath, imageService.readImage(filePath, configuration));

        logger.info("Read screenshot from file: {}", filePath);

        final Solution solution = solve(screenshot);

        logger.trace("solve:exit({})", solution);
        return solution;
    }

    public Solution solve(final Board board) throws BrickPopSolverException {
        logger.trace("solve:enter(board={})", board);

        Objects.requireNonNull(board, "board");

        logger.info("Solving board:{}{}", System.lineSeparator(), board);

        final SolutionService solutionService = configuration.getSolutionService();
        final Instant start = Instant.now();
        final Solution solution = solutionService.solve(board, configuration);
        final Instant end = Instant.now();

        if (solution.isEmpty()) {
            throw new SolutionException("No solution could be found");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Found a solution in {} seconds:{}{}", Duration.between(start, end).getSeconds(), System.lineSeparator(), solution);
        }

        logger.trace("solve:exit({})", solution);
        return solution;
    }

    private Path createScreenshotFile() throws BrickPopSolverException {
        final File tempFile;
        try {
            tempFile = File.createTempFile("brickpopsolver-", configuration.getImageFormat().getFileExtension());
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new BrickPopSolverException("Failed to create temporary file for screenshot", e);
        }

        logger.debug("Created temporary file for screenshot: {}", tempFile);

        return tempFile.toPath();
    }

    private Solution solve(final Screenshot screenshot) throws BrickPopSolverException {
        return solve(Board.fromScreenshot(screenshot, configuration));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        final BrickPopSolver other = (BrickPopSolver) obj;
        return new EqualsBuilder()
            .append(configuration, other.configuration)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(configuration)
            .hashCode();
    }
}
