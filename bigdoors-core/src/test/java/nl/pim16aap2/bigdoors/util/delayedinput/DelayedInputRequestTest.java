package nl.pim16aap2.bigdoors.util.delayedinput;

import lombok.SneakyThrows;
import nl.pim16aap2.bigdoors.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class DelayedInputRequestTest
{
    /**
     * Makes sure that delayed input requests time out properly after the defined timeout period.
     * <p>
     * Once timed out, the 'input' should be empty.
     */
    @Test
    @SneakyThrows
    void testTimeout()
    {
        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(100);
        final CompletableFuture<Optional<String>> output = request.getInputResult();
        final Optional<String> result = output.get(1000, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(request.timedOut());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @SneakyThrows
    void testDoubleInput()
    {
        final String firstInput = Util.randomInsecureString(10);
        final String secondInput = Util.randomInsecureString(10);

        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(5, TimeUnit.SECONDS);
        final CompletableFuture<Optional<String>> output = request.getInputResult();

        request.set(firstInput);
        request.set(secondInput);

        final Optional<String> result = output.get(100, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(request.success());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(firstInput, result.get());
        Assertions.assertNotEquals(secondInput, result.get());
    }

    /**
     * Makes sure that providing input works properly and returns it as intended.`
     */
    @Test
    @SneakyThrows
    void testInput()
    {
        final String inputString = Util.randomInsecureString(10);

        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(5, TimeUnit.SECONDS);
        final CompletableFuture<Optional<String>> output = request.getInputResult();

        request.set(inputString);

        final Optional<String> result = output.get(100, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(request.success());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(inputString, result.get());
    }

    @Test
    @SneakyThrows
    void testStatusCancelled()
    {
        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(5, TimeUnit.SECONDS);
        Assertions.assertEquals(DelayedInputRequest.Status.WAITING, request.getStatus());
        Assertions.assertFalse(request.success());
        Assertions.assertFalse(request.cancelled());
        Assertions.assertFalse(request.timedOut());
        Assertions.assertFalse(request.exceptionally());
        Assertions.assertFalse(request.completed());

        request.cancel();
        Assertions.assertEquals(DelayedInputRequest.Status.CANCELLED, request.getStatus());
        Assertions.assertFalse(request.success());
        Assertions.assertTrue(request.cancelled());
        Assertions.assertFalse(request.timedOut());
        Assertions.assertFalse(request.exceptionally());
        Assertions.assertTrue(request.completed());
    }

    @Test
    @SneakyThrows
    void testStatusTimedOut()
    {
        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(1, TimeUnit.MILLISECONDS);
        final long startTime = System.nanoTime();
        request.getInputResult().get(2, TimeUnit.SECONDS);
        final long duration = System.nanoTime() - startTime;
        // Ensure it took Much less than the 'allowed' 2 seconds.
        Assertions.assertTrue(duration < Duration.ofSeconds(1).toNanos());

        Assertions.assertEquals(DelayedInputRequest.Status.TIMED_OUT, request.getStatus());
        Assertions.assertFalse(request.success());
        Assertions.assertFalse(request.cancelled());
        Assertions.assertTrue(request.timedOut());
        Assertions.assertFalse(request.exceptionally());
        Assertions.assertTrue(request.completed());
    }

    @Test
    @SneakyThrows
    void testStatusSuccess()
    {
        final DelayedInputRequestImpl request = new DelayedInputRequestImpl(1, TimeUnit.SECONDS);
        request.set("VALUE");
        request.getInputResult().get(1, TimeUnit.SECONDS);
        Assertions.assertEquals(DelayedInputRequest.Status.COMPLETED, request.getStatus());
        Assertions.assertTrue(request.success());
        Assertions.assertFalse(request.cancelled());
        Assertions.assertFalse(request.timedOut());
        Assertions.assertFalse(request.exceptionally());
        Assertions.assertTrue(request.completed());
    }

    private static class DelayedInputRequestImpl extends DelayedInputRequest<String>
    {
        public DelayedInputRequestImpl(long timeout, TimeUnit timeUnit)
        {
            super(timeout, timeUnit);
        }

        public DelayedInputRequestImpl(long timeout)
        {
            this(timeout, TimeUnit.MILLISECONDS);
        }
    }
}
