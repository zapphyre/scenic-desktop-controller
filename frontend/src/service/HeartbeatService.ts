export class HeartbeatService {
    private static readonly HB_ENDPOINT = `${import.meta.env.VITE_API_BASE_URL}/hb`; // Adjust to your backend URL
    private lastUuid: string | null = null;
    private eventSource: EventSource | null = null;
    private isConnecting = false;

    public start() {
        this.connect();
    }

    private connect() {
        if (this.isConnecting) {
            return; // Prevent multiple simultaneous connections
        }
        this.isConnecting = true;

        this.eventSource = new EventSource(HeartbeatService.HB_ENDPOINT);

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

        this.eventSource.onerror = (e) => {
            this.eventSource?.close();
            this.eventSource = null;
            this.isConnecting = false;
            setTimeout(this.connect, 2100)
        };
    }
}

export const heartbeatService = new HeartbeatService();