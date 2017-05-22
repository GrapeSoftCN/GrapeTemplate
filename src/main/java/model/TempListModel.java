package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import apps.appsProxy;
import database.db;
import esayhelper.DBHelper;
import esayhelper.JSONHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class TempListModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();

	static {
		dbtemp = new DBHelper(appsProxy.configValue().get("db").toString(),
				"templateList","_id");
		_form = dbtemp.getChecker();
	}
	private db bind(){
		return dbtemp.bind(String.valueOf(appsProxy.appid()));
	}
	
	public TempListModel() {
		_form.putRule("name", formdef.notNull);
	}

	public int insert(JSONObject tempinfo) {
		if (!_form.checkRuleEx(tempinfo)) {
			return 1;
		}
		return bind().data(tempinfo).insertOnce() != null ? 0 : 99;
	}

	public int delete(String id) {
		return bind().eq("_id", new ObjectId(id)).delete() != null ? 0 : 99;
	}

	public String select() {
		return resultmessage(bind().limit(20).select());
	}

	public String select(String tempinfo) {
		JSONObject object = JSONHelper.string2json(tempinfo);
		for (Object object2 : object.keySet()) {
			bind().like(object2.toString(), object.get(object2.toString()));
		}
		return resultmessage(bind().limit(20).select());
	}

	public int update(String tid, JSONObject tempinfo) {
		return bind().eq("_id", new ObjectId(tid)).data(tempinfo)
				.update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONArray array = bind().page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) array.size() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return resultmessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(String tempinfo, int idx, int pageSize) {
		JSONObject info = JSONHelper.string2json(tempinfo);
		for (Object object2 : info.keySet()) {
			if ("_id".equals(object2.toString())) {
				bind().eq("_id", new ObjectId(info.get("_id").toString()));
			}
			bind().like(object2.toString(), info.get(object2.toString()));
		}
		JSONArray array = bind().dirty().page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) array.size() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return resultmessage(object);
	}

	@SuppressWarnings("unchecked")
	public int sort(String tid, long num) {
		JSONObject object = new JSONObject();
		object.put("sort", num);
		return bind().eq("_id", new ObjectId(tid)).data(object).update() != null
				? 0 : 99;
	}

	public int delete(String[] arr) {
		bind().or();
		int len = arr.length;
		for (int i = 0; i < len; i++) {
			bind().eq("_id", new ObjectId(arr[i]));
		}
		return bind().deleteAll() == len?0:99;
	}

	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map, JSONObject object) {
		if (map.entrySet() != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
						.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	private String resultmessage(JSONObject object) {
		_obj.put("records", object);
		return resultmessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultmessage(JSONArray array) {
		_obj.put("records", array);
		return resultmessage(0, _obj.toString());
	}

	public String resultmessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填字段没有填";
			break;
		default:
			msg = "其他操作异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
