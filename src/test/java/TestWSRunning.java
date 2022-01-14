
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;

import com.sun.tools.javac.Main;
import org.junit.BeforeClass;
import org.junit.Test;

import src.Httpserver;

public class TestWSRunning {
	private static Httpserver webServer;

	@Test
	public void getResourceResource_Empty() {
		Httpserver webServer = new Httpserver(new Socket());
		assertEquals("Test failed: getResourceResource_Empty",webServer.isAlive());

	}

}
