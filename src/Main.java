import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		for (int i = 0; i < 10; i ++){
			try {
				mouseMove(i * 20, i * 30);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
	public static void mouseMove(int x, int y) throws AWTException {
		Robot robot = new Robot();

		int mouseX = MouseInfo.getPointerInfo().getLocation().x;
		int mouseY = MouseInfo.getPointerInfo().getLocation().y;  

		if (Math.abs(x - 500) > 5)
			mouseX = mouseX + (int)((x - 200) * 0.08);
		if (Math.abs(y - 500) > 5)
			mouseY = mouseY - (int)((y - 300) * 0.02);

	        robot.mouseMove(mouseX, mouseY);

	        System.out.println(mouseX + ":" + mouseY);
	        return;
	    }
}