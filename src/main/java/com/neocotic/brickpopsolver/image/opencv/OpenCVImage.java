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

import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;

import com.neocotic.brickpopsolver.Color;
import com.neocotic.brickpopsolver.image.Image;
import com.neocotic.brickpopsolver.image.ImageFormat;

public final class OpenCVImage implements Image {

    private static final Logger LOG = LogManager.getLogger(OpenCVImage.class);

    private static final Color BLACK = new Color(0, 0, 0);

    private final ImageFormat format;
    private final Mat matrix;

    public OpenCVImage(final ImageFormat format, final Mat matrix) {
        this.format = Objects.requireNonNull(format, "format");
        this.matrix = Objects.requireNonNull(matrix, "matrix");
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public int getHeight() {
        return matrix.rows();
    }

    @Override
    public Color getPixel(final int x, final int y) {
        LOG.traceEntry("getPixel(x={}, y={})", x, y);

        final UByteIndexer indexer = matrix.createIndexer();

        try {
            final int blue = indexer.get(x, y, 0);
            final int green = indexer.get(x, y, 1);
            final int red = indexer.get(x, y, 2);

            return LOG.traceExit(new Color(red, green, blue));
        } finally {
            indexer.release();
        }
    }

    @Override
    public int getWidth() {
        return matrix.cols();
    }

    @Override
    public boolean isValid() {
        return matrix.channels() >= 3 && !BLACK.equals(getPixel(getWidth() / 2, getHeight() / 2));
    }
}
