package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.TemplateContextModel;

public class TemplateContext {
	private TemplateContextModel temp = new TemplateContextModel();
	private HashMap<String, Object> map = new HashMap<>();
	

	public TemplateContext() {
		map.put("ownid", 0);
		map.put("isdelete", 0);
		map.put("sort", 0);
		map.put("type", 1); // 模版类型 1：栏目模版；2：内容模版
		map.put("time", TimeHelper.nowMillis() + "");
	}

	public String TempInsert(String tempinfo) {
		JSONObject object = temp.AddMap(map, JSONHelper.string2json(tempinfo));
		return temp.resultMessage(temp.insert(object), "新增模版成功");
	}

	public String TempDelete(String tempID) {
		return temp.resultMessage(temp.delete(tempID), "删除模版成功");
	}

	public String TempBatchDelete(String tempid) {
		return temp.resultMessage(temp.delete(tempid.split(",")), "批量删除成功");
	}

	public String TempSelect() {
		return temp.resultMessage(temp.select());
	}

	public String TempFindByTid(String tid) {
		JSONObject object = temp.find(tid);
		String name = "";
		if (object != null) {
			name = object.get("name").toString();
		}
		return name;
	}

	public String TempFindByType(String type) {
		return temp.resultMessage(temp.search(type));
	}

	public String TempFind(String tempinfo) {
		return temp.resultMessage(temp.select(tempinfo));
	}

	public String TempUpdate(String tempid, String tempinfo) {
		return temp.resultMessage(temp.update(tempid, JSONHelper.string2json(tempinfo)), "模版更新成功");
	}

	public String TempPage(int idx, int pageSize) {
		return temp.resultMessage(temp.page(idx, pageSize));
	}

	public String TempPageBy(int idx, int pageSize, String tempinfo) {
		return temp.resultMessage(temp.page(tempinfo, idx, pageSize));
	}

	public String TempSort(String tempid, long num) {
		return temp.resultMessage(temp.sort(tempid, num), "设置排序值成功");
	}

	public String TempSetTid(String tempid, String tid) {
		return temp.resultMessage(temp.setTid(tempid, tid), "设置模版方案成功");
	}

}
