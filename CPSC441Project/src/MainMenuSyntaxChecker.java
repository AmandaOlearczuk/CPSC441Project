

public class MainMenuSyntaxChecker {
	
	private int login = 1;
	private int signup = 2;
	private int joinroom=3;
	private int hostroom=4;
	private int searchforroom=5;
	private int exit=6;
	 
	private int[] mainMenuOptions = new int[]{login,signup,joinroom,hostroom,searchforroom,exit};

	public MainMenuSyntaxChecker() {
		
	}
	
	/**
	 * This method checks if option is a valid main menu option
	 * @param String - option code
	 * @return boolean - true or false
	 */
	public boolean isMainMenuOption(String option) {
		try { 
			int result = Integer.parseInt(option);
			for (int i : mainMenuOptions) if (i == result) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}

	public boolean isMainMenuLogIn(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == login) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}
	
	public boolean isMainMenuSignUp(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == signup) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}
	
	public boolean isMainMenuJoinRoom(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == joinroom) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}
	
	public boolean isMainMenuHostRoom(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == hostroom) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}
	
	public boolean isMainMenuSearchForRoom(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == searchforroom) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}
	
	/**
	 * Checks if option selected is exit
	 * @param option
	 * @return boolean
	 */
	public boolean isMainMenuExit(String option) {
		try { 
			int result = Integer.parseInt(option);
			if (result == exit) return true;
			return false;	
				
		} catch (Exception e){
			//option is not an integer
			return false;
		}
	}

}


