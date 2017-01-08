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
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.device.DeviceService;
import com.neocotic.brickpopsolver.device.Screenshot;
import com.neocotic.brickpopsolver.image.ImageService;
import com.neocotic.brickpopsolver.service.ServiceNotFoundException;
import com.neocotic.brickpopsolver.solution.Solution;
import com.neocotic.brickpopsolver.solution.SolutionException;
import com.neocotic.brickpopsolver.solution.SolutionService;

public final class BrickPopSolver {

    private static final Logger LOG = LogManager.getLogger(BrickPopSolver.class);

    private static final String PROPERTY_PREFIX = "brickpopsolver.";

    public static void main(final String[] args) throws Exception {
        final BrickPopSolver solver = new BrickPopSolver();
        if (ArrayUtils.isEmpty(args)) {
            solver.solve().play();
        } else {
            solver.solve(Paths.get(args[0]));
        }
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

        LOG.debug("BrickPopSolver created with configuration:{}{}", SystemUtils.LINE_SEPARATOR, configuration);
    }

    public Solution solve() throws BrickPopSolverException {
        LOG.traceEntry();

        final DeviceService deviceService = configuration.getDeviceService();
        final Set<String> devices = deviceService.getDevices(configuration);

        LOG.info("{} connected devices found", devices::size);

        final Screenshot screenshot = deviceService.captureScreenshot(createScreenshotFile(), configuration);

        LOG.info("Captured screenshot from device: {}", screenshot);

        return LOG.traceExit(solve(screenshot));
    }

    public Solution solve(final Path filePath) throws BrickPopSolverException {
        LOG.traceEntry("solve(filePath={})", filePath);

        Objects.requireNonNull(filePath, "filePath");

        final ImageService imageService = configuration.getImageService();
        final Screenshot screenshot = new Screenshot(filePath, imageService.readImage(filePath, configuration));

        LOG.info("Read screenshot from file: {}", filePath);

        return LOG.traceExit(solve(screenshot));
    }

    public Solution solve(final Board board) throws BrickPopSolverException {
        LOG.traceEntry("solve(board={})", board);

        Objects.requireNonNull(board, "board");

        LOG.info("Solving board: {}", board);

        final SolutionService solutionService = configuration.getSolutionService();
        final Instant start = Instant.now();
        final Solution solution = solutionService.solve(board, configuration);
        final Instant end = Instant.now();

        if (solution.isEmpty()) {
            throw new SolutionException("No solution could be found");
        }

        LOG.info("Found a solution in {} seconds:{}{}", () -> Duration.between(start, end).getSeconds(), () -> SystemUtils.LINE_SEPARATOR, () -> solution);

        return LOG.traceExit(solution);
    }

    private Path createScreenshotFile() throws BrickPopSolverException {
        final File tempFile;
        try {
            tempFile = File.createTempFile("brickpopsolver-", configuration.getImageFormat().getFileExtension());
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new BrickPopSolverException("Failed to create temporary file for screenshot", e);
        }

        LOG.debug("Created temporary file for screenshot: {}", tempFile);

        return tempFile.toPath();
    }

    private Solution solve(final Screenshot screenshot) throws BrickPopSolverException {
        return solve(screenshot.createBoard(configuration));
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
