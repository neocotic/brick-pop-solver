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
package com.neocotic.brickpopsolver.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neocotic.brickpopsolver.CustomToStringStyle;

public final class Command {

    private static final Logger LOG = LogManager.getLogger(Command.class);

    private final String command;

    public Command(final String command) {
        this.command = Objects.requireNonNull(command, "command");

        LOG.debug("Command created with command: {}", command);
    }

    public CommandResult run(Object... args) throws CommandException {
        LOG.traceEntry("run(args={})", new Object[]{args});

        args = args != null ? args : new Object[0];

        final List<String> arguments = new ArrayList<>(args.length + 1);
        arguments.add(command);

        for (final Object arg : args) {
            arguments.add(String.valueOf(arg));
        }

        LOG.info("Executing command: {}", () -> StringUtils.join(arguments.toArray(), ' '));

        try {
            final Process process = new ProcessBuilder(arguments).start();
            return LOG.traceExit(new CommandResult(this, process));
        } catch (IOException e) {
            throw new CommandException(String.format("%s command failed to execute with args: %s", command, ArrayUtils.toString(args)), e);
        }
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, CustomToStringStyle.SHORT_STYLE)
            .append("command", command)
            .toString();
    }
}
