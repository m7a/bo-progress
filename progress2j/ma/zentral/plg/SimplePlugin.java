package ma.zentral.plg;


/**
 * Allows you to create simple plugins which do not need to interact
 * with Ma_Zentral and which do not need to access settings.
 * 
 * @author Linux-Fan, Ma_Sys.ma
 * @version 1.0
 * @since Ma_Zentral 11.0.3.0
 * @see ma.zentral.plg.Plugin
 */
public interface SimplePlugin {

	/**
	 * Start signal. This is used when your plugin is
	 * started via it's button. It automatically gets
	 * an own thread.
	 * 
	 * @see #signal(int)
	 */
	public static final int SIGNAL_START = 1;
	
	/**
	 * This signal is send when your program is to be
	 * ended. You may do all cleaning actions here.
	 * 
	 * Let the method wait until you are ready cleaning.
	 * 
	 * @see #signal(int)
	 */
	public static final int SIGNAL_TERM = 4;
	
	/**
	 * This signal tells your plugin to end it <i>immediatly</i>.
	 * Do only the importantest cleaning actions here.
	 * 
	 * @see #signal(int)
	 */
	public static final int SIGNAL_KILL = 5;
	
	/**
	 * Allows Ma_Zentral or the user to send signals to your plugin.
	 * A signal can be one of the constants defined above.
	 *
	 * On Exit Ma_Zentral usually sends the TERM Signal to all
	 * plugins (each plugin get's an extra thread here).
	 * Then it waits 3000 ms and sends the KILL Signal to all
	 * plugins which are not ready cleaning. Then Ma_Zentral
	 * terminates the application after 1000 ms.
	 *
	 * @param signal Signal constant
	 */
	public abstract void signal(int signal);
	
}
