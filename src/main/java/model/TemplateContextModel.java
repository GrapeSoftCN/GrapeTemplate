package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.JSONHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class TemplateContextModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();

	static {
		dbtemp = new DBHelper("mongodb", "tempcontext");
		_form = dbtemp.getChecker();
	}

	public TemplateContextModel() {
		_form.putRule("name", formdef.notNull);
		_form.putRule("time", formdef.notNull);
	}

	public int insert(JSONObject tempinfo) {
		if (!_form.checkRuleEx(tempinfo)) {
			return 1;
		}
		return dbtemp.data(tempinfo).insertOnce() != null ? 0 : 99;
	}

	public int delete(String id) {
		return dbtemp.eq("_id", new ObjectId(id)).delete() != null ? 0 : 99;
	}

	public int update(String tempid, JSONObject tempInfo) {
		return dbtemp.eq("_id", new ObjectId(tempid)).data(tempInfo).update() != null ? 0 : 99;
	}

	public JSONArray select() {
		return dbtemp.limit(20).select();
	}

	public JSONObject find(String tid) {
		return dbtemp.eq("_id", new ObjectId(tid)).find();
	}

	public JSONArray select(String tempinfo) {
		JSONObject object = JSONHelper.string2json(tempinfo);
		for (Object object2 : object.keySet()) {
			dbtemp.like(object2.toString(), object.get(object2.toString()));
		}
		return dbtemp.limit(20).select();
	}

	// 根据模版类型显示模版
	public JSONArray search(String type) {
		return dbtemp.eq("type", type).limit(20).select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = dbtemp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) dbtemp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(String tempinfo, int idx, int pageSize) {
		Set<Object> set = JSONHelper.string2json(tempinfo).keySet();
		for (Object object2 : set) {
			dbtemp.eq(object2.toString(), JSONHelper.string2json(tempinfo).get(object2.toString()));
		}
		JSONArray array = dbtemp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) dbtemp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public int sort(String tempid, long num) {
		JSONObject object = new JSONObject();
		object.put("sort", num);
		return dbtemp.eq("_id", new ObjectId(tempid)).data(object).update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public int setTid(String tempid, String tid) {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		return dbtemp.eq("_id", new ObjectId(tempid)).data(object).update() != null ? 0 : 99;
	}

	public int delete(String[] arr) {
		dbtemp.or();
		for (int i = 0; i < arr.length; i++) {
			dbtemp.eq("_id", new ObjectId(arr[i]));
		}
		return dbtemp.deleteAll() == arr.length ? 0 : 99;
	}

	/**
	 * 生成32位随机编码
	 * 
	 * @return
	 */
	public static String getID() {
		String str = UUID.randomUUID().toString().trim();
		return str.replace("-", "");
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
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}

	public String resultMessage(int num, String msg) {
		String message = null;
		switch (num) {
		case 0:
			message = msg;
			break;
		case 1:
			message = "必填项没有填";
			break;
		default:
			message = "其他异常";
		}
		return jGrapeFW_Message.netMSG(num, message);
	}
}
