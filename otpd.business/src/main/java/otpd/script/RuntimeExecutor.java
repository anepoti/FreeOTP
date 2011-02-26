/*
*  
*	FreeOTP OATH OneTimePassword Solution - Copyright (c) 2010, Alessandro Nepoti (alessandro.nepoti@wedjaa.net) 
*	All rights reserved.
*	
*	Redistribution and use in source and binary forms, with or without
*	modification, are permitted provided that the following conditions are met:
*	* Redistributions of source code must retain the above copyright
*	  notice, this list of conditions and the following disclaimer.
*	* Redistributions in binary form must reproduce the above copyright
*	  notice, this list of conditions and the following disclaimer in the
*	  documentation and/or other materials provided with the distribution.
*	* Neither the name of the <organization> nor the
*	  names of its contributors may be used to endorse or promote products
*	  derived from this software without specific prior written permission.
*	
*	THIS SOFTWARE IS PROVIDED BY COPYRIGHT HOLDERS ''AS IS'' AND ANY
*	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
*	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*	DISCLAIMED. IN NO EVENT SHALL <copyright holder> BE LIABLE FOR ANY
*	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
*	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
*	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
*	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
*	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
*	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*/

package otpd.script;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

 

public class RuntimeExecutor {

    private long timeout = Long.MAX_VALUE;
    private String command = "";

    /**
     * Default constructor - Timeout set to Long.MAX_VALUE
     */
    public RuntimeExecutor() {
    }

    /**
     * Constructor
     * @param timeout Set the timeout for the external application to run
     */
    public RuntimeExecutor(long timeout) {
        this.timeout = timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String executeSecureTransfer(String... parameters) throws IOException, TimeoutException {

        StringBuffer commandBuffer = new StringBuffer();
        List params = Arrays.asList(parameters);

        commandBuffer.append(command);

        for (Iterator<String> i = params.iterator(); i.hasNext();) {
            commandBuffer.append(" ");
            commandBuffer.append(i.next());
        }
        return execute(commandBuffer.toString(), null);
    }

    /**
     * Execute a Runtime process
     * @param command - The command to execute
     * @param env - Environment variables to put in the Runtime process
     * @return The output from the process
     * @throws IOException
     * @throws TimeoutException - Process timed out and did not return in the specified amount of time
     */
    private String execute(String command, String[] env) throws IOException, TimeoutException {


        Process p = Runtime.getRuntime().exec(command, env);

        // Set a timer to interrupt the process if it does not return within the timeout period
        Timer timer = new Timer();
        timer.schedule(new InterruptScheduler(Thread.currentThread()), this.timeout);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // Stop the process from running
            p.destroy();
            throw new TimeoutException(command + " did not return after " + this.timeout + " milliseconds");
        } finally {
            // Stop the timer
            timer.cancel();
        }

        // Get the output from the external application
        StringBuilder buffer = new StringBuilder();
        BufferedInputStream br = new BufferedInputStream(p.getInputStream());
        while (br.available() != 0) {
            buffer.append((char) br.read());
        }
        String res = buffer.toString().trim();
        return res;

    }

    private class InterruptScheduler extends TimerTask {

        Thread target = null;

        public InterruptScheduler(Thread target) {
            this.target = target;
        }

        @Override
        public void run() {
            target.interrupt();
        }
    }

	 
}
