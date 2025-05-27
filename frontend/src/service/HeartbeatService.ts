export class HeartbeatService {
    private static readonly HB_ENDPOINT = `${import.meta.env.VITE_API_BASE_URL}/hb`; // Adjust to your backend URL
    private lastUuid: string | null = null;
    private eventSource: EventSource | null = null;
    private isConnecting = false;

    private scheduleRetry() {
        this.eventSource?.close();
        this.eventSource = null;
        this.isConnecting = false;
        setTimeout(this.connect.bind(this), 2100)
    }

    public start() {
        this.connect();
    }

    private connect() {
        if (this.isConnecting) {
            return; // Prevent multiple simultaneous connections
        }
        this.isConnecting = true;

        try {
            this.eventSource = new EventSource(HeartbeatService.HB_ENDPOINT);
        } catch (e) {
            this.scheduleRetry();
            return;
        }

        this.eventSource.onmessage = (event: MessageEvent) => {
            const newUuid = event.data;
            console.log('Received UUID:', newUuid);

            console.log("lastUuid", this.lastUuid);
            if (this.lastUuid && this.lastUuid !== newUuid) {
                console.log('UUID changed, reloading page...');
                window.location.reload();
            }
            this.lastUuid = newUuid;
        };

        this.eventSource.onopen = () => {
            console.log('EventSource connected');
            this.isConnecting = false;
        };

        this.eventSource.onerror = (e) => this.scheduleRetry();
    }
}

export const heartbeatService = new HeartbeatService();