package interfaceApplication;

import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import model.TemplateContextModel;

@SuppressWarnings("unchecked")
public class TemplateContext {
	private TemplateContextModel temp = new TemplateContextModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject _obj = new JSONObject();

	public TemplateContext() {
		map.put("tempid", TemplateContextModel.getID());
		map.put("ownid", 0);
		map.put("isdelete", 0);
		map.put("sort", 0);
	}

	public String TempInsert(String tempinfo) {
		JSONObject object = temp.AddMap(map, JSONHelper.string2json(tempinfo));
		return temp.resultMessage(temp.insert(object), "新增模版成功");
	}

	public String TempDelete(String tempID) {
		return temp.resultMessage(temp.delete(tempID), "删除模版成功");
	}

	public String TempSelect() {
		_obj.put("record", temp.select());
		return StringEscapeUtils.unescapeJava(temp.resultMessage(0, _obj.toString()));
	}

	public String TempFind(String tempinfo) {
		_obj.put("record", temp.select(tempinfo));
		return StringEscapeUtils.unescapeJava(temp.resultMessage(0, _obj.toString()));
	}

	public String TempUpdate(String tempid, String tempinfo) {
		return temp.resultMessage(temp.update(tempid, JSONHelper.string2json(tempinfo)),
				"模版更新成功");
	}

	public String TempPage(int idx, int pageSize) {
		_obj.put("record", temp.page(idx, pageSize));
		return StringEscapeUtils.unescapeJava(temp.resultMessage(0, _obj.toString()));
	}

	public String TempPageBy(int idx, int pageSize, String tempinfo) {
		_obj.put("record", temp.page(tempinfo, idx, pageSize));
		return StringEscapeUtils.unescapeJava(temp.resultMessage(0, _obj.toString()));
	}

	public String TempSort(String tempid, long num) {
		return temp.resultMessage(temp.sort(tempid, num), "设置排序值成功");
	}

	public String TempSetTid(String tempid, String tid) {
		return temp.resultMessage(temp.setTid(tempid, tid), "设置模版方案成功");
	}

	public String TempBatchDelete(String tempid) {
		return temp.resultMessage(temp.delete(tempid.split(",")), "批量删除成功");
	}
}
