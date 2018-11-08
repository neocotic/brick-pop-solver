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

import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.neocotic.brickpopsolver.device.DeviceService;
import com.neocotic.brickpopsolver.device.Point;
import com.neocotic.brickpopsolver.device.android.AndroidDeviceService;
import com.neocotic.brickpopsolver.image.ImageFormat;
import com.neocotic.brickpopsolver.image.ImageService;
import com.neocotic.brickpopsolver.image.opencv.OpenCVImageService;
import com.neocotic.brickpopsolver.service.ServiceManager;
import com.neocotic.brickpopsolver.service.ServiceNotFoundException;
import com.neocotic.brickpopsolver.solution.SolutionService;
import com.neocotic.brickpopsolver.solution.parallel.ParallelSolutionService;

public final class Configuration {

    public static final String DEFAULT_DEVICE_SERVICE_NAME = AndroidDeviceService.SERVICE_NAME;
    public static final String DEFAULT_IMAGE_FORMAT_NAME = ImageFormat.PNG.name();
    public static final String DEFAULT_IMAGE_SERVICE_NAME = OpenCVImageService.SERVICE_NAME;
    public static final int DEFAULT_OFFSET = 102;
    public static final String DEFAULT_SOLUTION_SERVICE_NAME = ParallelSolutionService.SERVICE_NAME;
    public static final int DEFAULT_START_X = 86;
    public static final int DEFAULT_START_Y = 485;

    private final DeviceService deviceService;
    private final ImageFormat imageFormat;
    private final ImageService imageService;
    private final int offset;
    private final SolutionService solutionService;
    private final Point start;

    public Configuration(final DeviceService deviceService, final ImageService imageService, final SolutionService solutionService, final ImageFormat imageFormat, final Integer offset, final Point start) {
        this.deviceService = Objects.requireNonNull(deviceService, "deviceService");
        this.imageService = Objects.requireNonNull(imageService, "imageService");
        this.solutionService = Objects.requireNonNull(solutionService, "solutionService");
        this.imageFormat = Objects.requireNonNull(imageFormat, "imageFormat");
        this.offset = Objects.requireNonNull(offset, "offset");
        this.start = Objects.requireNonNull(start, "start");
    }

    public Configuration(final String deviceServiceName, final String imageServiceName, final String solutionServiceName, final String imageFormatName, final Integer offset, final Integer startX, final Integer startY) throws ServiceNotFoundException {
        this.deviceService = ServiceManager.getService(DeviceService.class, deviceServiceName != null ? deviceServiceName : DEFAULT_DEVICE_SERVICE_NAME);
        this.imageService = ServiceManager.getService(ImageService.class, imageServiceName != null ? imageServiceName : DEFAULT_IMAGE_SERVICE_NAME);
        this.solutionService = ServiceManager.getService(SolutionService.class, solutionServiceName != null ? solutionServiceName : DEFAULT_SOLUTION_SERVICE_NAME);
        this.imageFormat = ImageFormat.valueOf(imageFormatName != null ? imageFormatName : DEFAULT_IMAGE_FORMAT_NAME);
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
        this.start = new Point(startX != null ? startX : DEFAULT_START_X, startY != null ? startY : DEFAULT_START_Y);
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public int getOffset() {
        return offset;
    }

    public SolutionService getSolutionService() {
        return solutionService;
    }

    public Point getStart() {
        return start;
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

        final Configuration other = (Configuration) obj;
        return new EqualsBuilder()
            .append(deviceService, other.deviceService)
            .append(imageFormat, other.imageFormat)
            .append(imageService, other.imageService)
            .append(offset, other.offset)
            .append(solutionService, other.solutionService)
            .append(start, other.start)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(deviceService)
            .append(imageFormat)
            .append(imageService)
            .append(offset)
            .append(solutionService)
            .append(start)
            .hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, CustomToStringStyle.LONG_STYLE)
            .append("deviceService", deviceService)
            .append("imageFormat", imageFormat)
            .append("imageService", imageService)
            .append("offset", offset)
            .append("solutionService", solutionService)
            .append("start", start)
            .toString();
    }
}
