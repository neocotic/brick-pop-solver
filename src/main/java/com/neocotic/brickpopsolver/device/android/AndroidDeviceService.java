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
package com.neocotic.brickpopsolver.device.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.command.Command;
import com.neocotic.brickpopsolver.command.CommandException;
import com.neocotic.brickpopsolver.device.DeviceException;
import com.neocotic.brickpopsolver.device.DeviceService;
import com.neocotic.brickpopsolver.device.Point;
import com.neocotic.brickpopsolver.device.Screenshot;
import com.neocotic.brickpopsolver.image.Image;
import com.neocotic.brickpopsolver.image.ImageException;
import com.neocotic.brickpopsolver.service.AbstractService;

public class AndroidDeviceService extends AbstractService implements DeviceService {

    private static final Logger LOG = LogManager.getLogger(AndroidDeviceService.class);

    public static final String SERVICE_NAME = "android";

    private static final Pattern DEVICE_ID_REGEX = Pattern.compile("^(\\S+).*");

    private final Command adb = new Command("adb");

    @Override
    public Screenshot captureScreenshot(final Path filePath, final Configuration configuration) throws DeviceException {
        LOG.traceEntry("captureScreenshot(filePath={}, configuration={})", filePath, configuration);

        LOG.debug("Capturing screenshot from device via ADB and saving to file: {}", filePath);

        try (final InputStream input = new BufferedInputStream(adb.run("shell", "screencap", "-p").getInputStream())) {
            Files.write(filePath, IOUtils.toByteArray(input));

            final Image image = configuration.getImageService().readImage(filePath, configuration);

            LOG.debug("Screenshot captured in file: {}", filePath);

            return LOG.traceExit(new Screenshot(filePath, image));
        } catch (CommandException | IOException e) {
            throw new DeviceException("Failed to capture screenshot from ADB", e);
        } catch (ImageException e) {
            throw new DeviceException("Failed to read captured screenshot", e);
        }
    }

    @Override
    public Set<String> getDevices(final Configuration configuration) throws DeviceException {
        LOG.traceEntry("getDevices(configuration={})", configuration);

        LOG.debug("Reading devices from ADB");

        try (final InputStream input = adb.run("devices").getInputStream()) {
            @SuppressWarnings("unchecked")
            final List<String> lines = IOUtils.readLines(input);
            lines.remove(0);
            final Set<String> devices = lines.stream()
                .map(line -> {
                    final Matcher matcher = DEVICE_ID_REGEX.matcher(line);
                    return matcher.find() ? matcher.group(1) : null;
                })
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

            LOG.debug("Connected devices found: {}", devices);

            return LOG.traceExit(devices);
        } catch (CommandException | IOException e) {
            throw new DeviceException("Failed to read devices from ADB", e);
        }
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void triggerPoint(final Point point, final Configuration configuration) throws DeviceException {
        LOG.traceEntry("triggerPoint(point={}, configuration={})", point, configuration);

        LOG.debug("Triggering tap on device at {} via ADB", point);

        try {
            adb.run("shell", "input", "tap", point.getX(), point.getY()).verify();

            Thread.sleep(1200);
        } catch (CommandException e) {
            throw new DeviceException("", e);
        } catch (InterruptedException e) {
            throw new DeviceException("Interrupted after triggering point: " + point, e);
        }

        LOG.traceExit();
    }
}
