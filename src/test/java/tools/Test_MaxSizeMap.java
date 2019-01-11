package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.b07z.sepia.server.core.data.CmdMap;
import net.b07z.sepia.server.core.java.MaxSizeMap;
import net.b07z.sepia.server.core.tools.ThreadManager;

public class Test_MaxSizeMap {
	
	private static final class UserTestData {
		public String name;
		public List<CmdMap> map;
		public UserTestData(String name, List<CmdMap> map){
			this.name = name;
			this.map = map;
		}
	}

	public static void main(String[] args) throws Exception {
		
		//TODO: make this a real JUnit test
		
		Map<String, List<CmdMap>> map = MaxSizeMap.getSynchronizedMap(5);

		List<String> users = Arrays.asList("uid1", "uid2", "uid3", "uid4", "uid5", "uid6", "uid7", "uid8", "uid9", "uid10");
		List<UserTestData> data = new ArrayList<>();
		for (String u : users){
			List<CmdMap> cmdm = new ArrayList<>();
			cmdm.add(new CmdMap(u + ".anyCmd", new ArrayList<>(), Arrays.asList("all")));
			data.add(new UserTestData(u, cmdm));
		}
		boolean success = ThreadManager.runTasks(3, (Object o) -> {
			long slp = Math.round(Math.random() * 100);
			try{ Thread.sleep(slp); } catch (Exception e) {}
			UserTestData utd = (UserTestData) o;
			map.put(utd.name, utd.map);
			System.out.println(utd.name + " - " + slp); 		//DEBUG
		}, data, 5000l);
		
		System.out.println("Success? " + success);
		System.out.println(map.size());
		System.out.println(map.keySet());
		
		map.put(data.get(0).name, data.get(0).map);
		map.put(data.get(1).name, data.get(1).map);
		System.out.println(map);
	}

}
