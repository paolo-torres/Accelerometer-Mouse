import java.awt.*;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;

public class Mouse implements SerialPortEventListener {
    SerialPort serialPort;
    int avgX = 0;
    int avgY = 0;

    private static final String PORT_NAMES[] = { 
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM4", // Windows
    };

    /**
    * A BufferedReader which will be fed by an InputStreamReader 
    * converting the bytes into characters 
    * making the displayed results codepage independent
    */
    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    int buttonOld = 1;
    
    public void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }        
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
    * This should be called when you stop using the port.
    * This will prevent port locking on platforms like Linux.
    */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it. In this case, it calls the mouseMove method.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                mouseMove(inputLine);
                System.out.println("********************");
                //System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but should consider the other ones.
    }

    public static void main(String[] args) throws Exception {
        Mouse main = new Mouse();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                // The following line will keep this app alive for 1000 seconds,
                // waiting for events to occur and responding to them (printing incoming messages to console).
                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        t.start();
        System.out.println("Started");
    }
    
    // My method mouseMove, takes in a string containing the three data points and operates the mouse in turn
    public void mouseMove(String data) throws AWTException {
        int index1 = data.indexOf(" ", 0);
        int index2 = data.indexOf(" ", index1 + 1);
        int xCord = Integer.valueOf(data.substring(index1 + 1 , index2));
        int yCord = Integer.valueOf(data.substring(0, index1));
        int button = Integer.valueOf(data.substring(index2 + 1));
        Robot robot = new Robot();

		int mouseX = MouseInfo.getPointerInfo().getLocation().x;
        int mouseY = MouseInfo.getPointerInfo().getLocation().y;
        
        double xTune = 0.0001;
        double yTune = 0.0001;

        if (button == 0) {
            if (buttonOld == 1) {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(10);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);  
            }
        }

        if (Math.abs(xCord - 500) > 5)
            mouseX = mouseX + (int)((xCord) * 10);
        if (Math.abs(yCord - 500) > 5)
            mouseY = mouseY - (int)((yCord) * 10);

        robot.mouseMove(mouseX, mouseY);

        buttonOld = button;
        System.out.println(xCord + ":" + yCord + ":" + button + ":" + mouseX + ":" + mouseY);
        return;
    }
}