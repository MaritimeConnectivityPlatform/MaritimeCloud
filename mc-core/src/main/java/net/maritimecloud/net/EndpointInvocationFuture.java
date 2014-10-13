/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.maritimecloud.net;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A future interface returned when invoking a remote endpoint method.
 *
 * @author Kasper Nielsen
 */
public interface EndpointInvocationFuture<T> extends DispatchedMessage, Future<T> {

    /**
     * Returns the result value (or throws any encountered exception) if completed, else returns the given
     * valueIfAbsent.
     *
     * @param valueIfAbsent
     *            the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException
     *             if the computation was cancelled
     */
    T getNow(T valueIfAbsent);

    /**
     * The given function is invoked with the result (or {@code null} if none) and the exception (or {@code null} if
     * none) of this NetworkFuture when complete.
     *
     * @param consumer
     *            the composer used for processing the result
     */
    void handle(BiConsumer<T, Throwable> consumer);

    T join();

    <U> EndpointInvocationFuture<U> thenApply(Function<? super T, ? extends U> fn);

    void thenRun(Runnable action);

    /**
     * Creates a new EndpointInvocationFuture that will time out via {@link TimeoutException} if this task has not
     * completed within the specified time.
     *
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return the new future
     */
    EndpointInvocationFuture<T> timeout(long timeout, TimeUnit unit);
}