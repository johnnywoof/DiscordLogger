package me.johnnywoof.discordlogger;

class FlushLogHandlerTask implements Runnable {
    private final NativeEnvironment nativeEnvironment;

    public FlushLogHandlerTask(NativeEnvironment nativeEnvironment) {
        this.nativeEnvironment = nativeEnvironment;
    }

    @Override
    public void run() {
        this.nativeEnvironment.flushLogHook();
    }
}
