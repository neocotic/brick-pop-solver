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
package com.neocotic.brickpopsolver.image.opencv;

import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_imgcodecs;

import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.image.Image;
import com.neocotic.brickpopsolver.image.ImageException;
import com.neocotic.brickpopsolver.image.ImageService;
import com.neocotic.brickpopsolver.service.AbstractService;

public class OpenCVImageService extends AbstractService implements ImageService {

    private static final Logger LOG = LogManager.getLogger(OpenCVImageService.class);

    public static final String SERVICE_NAME = "opencv";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public Image readImage(final Path filePath, final Configuration configuration) throws ImageException {
        LOG.traceEntry("readImage(filePath={}, configuration={})", filePath, configuration);

        LOG.debug("Reading image from file: {}", filePath);

        final Image image = new OpenCVImage(configuration.getImageFormat(), opencv_imgcodecs.imread(filePath.toAbsolutePath().toString()));
        if (!image.isValid()) {
            throw new ImageException("Image is not valid: " + filePath);
        }

        LOG.debug("Valid image read from file: {}", filePath);

        return LOG.traceExit(image);
    }
}
