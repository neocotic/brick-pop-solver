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
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CommandResult {

    private static final Logger LOG = LogManager.getLogger(CommandResult.class);

    private final Command command;
    private final Process process;

    public CommandResult(final Command command, final Process process) {
        this.command = Objects.requireNonNull(command, "command");
        this.process = Objects.requireNonNull(process, "process");
    }

    public void verify() throws CommandException {
        LOG.traceEntry();

        try {
            final int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new CommandException(String.format("%s command failed with code[%d]: %s", command, exitValue, getErrorMessage()));
            }
        } catch (InterruptedException | IOException e) {
            throw new CommandException(String.format("%s command failed", command), e);
        }

        LOG.traceExit();
    }

    public Command getCommand() {
        return command;
    }

    public String getErrorMessage() throws IOException {
        return StringUtils.trimToEmpty(IOUtils.toString(process.getErrorStream()));
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    public InputStream getInputStream() {
        return process.getInputStream();
    }

    public Process getProcess() {
        return process;
    }
}
