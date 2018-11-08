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
package com.neocotic.brickpopsolver.solution;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neocotic.brickpopsolver.Configuration;
import com.neocotic.brickpopsolver.Coordinate;
import com.neocotic.brickpopsolver.CustomToStringStyle;
import com.neocotic.brickpopsolver.device.DeviceException;
import com.neocotic.brickpopsolver.device.Point;

public final class Solution {

    private static final Logger logger = LoggerFactory.getLogger(Solution.class);

    private final Configuration configuration;
    private final List<Coordinate> steps;

    public Solution(final Configuration configuration) {
        this(configuration, null);
    }

    public Solution(final Configuration configuration, final List<Coordinate> steps) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.steps = steps;
    }

    public boolean isEmpty() {
        return steps == null || steps.isEmpty();
    }

    public Solution play() throws SolutionException {
        logger.trace("play:enter()");

        if (isEmpty()) {
            throw new SolutionException("Solution is empty");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Playing solution of {} steps", steps.size());
        }

        int index = 0;
        final int offset = configuration.getOffset();
        final Point start = configuration.getStart();

        for (final Coordinate step : steps) {
            index++;

            final Point point = start.offset(offset * step.getColumn(), offset * step.getRow());

            logger.debug("Playing step {}: {}", index, point);

            try {
                configuration.getDeviceService().triggerPoint(point, configuration);
            } catch (DeviceException e) {
                throw new SolutionException("Failed to play step: " + index, e);
            }
        }

        logger.trace("play:exit({})", this);
        return this;
    }

    public List<Coordinate> getSteps() {
        return steps;
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

        final Solution other = (Solution) obj;
        return new EqualsBuilder()
            .append(configuration, other.configuration)
            .append(steps, other.steps)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(configuration)
            .append(steps)
            .hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, CustomToStringStyle.SHORT_STYLE)
            .append("steps", steps)
            .toString();
    }
}
