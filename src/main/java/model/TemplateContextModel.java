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

public class TemplateContextModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();

	static {
		dbtemp = new DBHelper(appsProxy.configValue().get("db").toString(),
				"templateContect");
		_form = dbtemp.getChecker();
	}

	private db bind(){
		return dbtemp.bind(String.valueOf(appsProxy.appid()));
	}
	public TemplateContextModel() {
		_form.putRule("name", formdef.notNull);
		_form.putRule("time", formdef.notNull);
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

	public int update(String tempid, JSONObject tempInfo) {
		return bind().eq("_id", new ObjectId(tempid)).data(tempInfo)
				.update() != null ? 0 : 99;
	}

	public JSONObject find(String tid) {
		return bind().eq("_id", new ObjectId(tid)).find();
	}

	public String select(String tempinfo) {
		JSONObject object = JSONHelper.string2json(tempinfo);
		for (Object object2 : object.keySet()) {
			if ("_id".equals(object2.toString())) {
				bind().eq("_id", new ObjectId(object.get("_id").toString()));
			}
			bind().like(object2.toString(), object.get(object2.toString()));
		}
		JSONArray array = bind().limit(20).select();
		return resultMessage(array);
	}

	// 根据模版类型显示模版
	public String search(String type) {
		JSONArray array = bind().eq("type", type).limit(20).select();
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONArray array = bind().page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) bind().count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(String tempinfo, int idx, int pageSize) {
		JSONObject Info = JSONHelper.string2json(tempinfo);
		for (Object object2 : Info.keySet()) {
			if ("_id".equals(object2.toString())) {
				bind().eq("_id", new ObjectId(Info.get("_id").toString()));
			}
			bind().eq(object2.toString(), Info.get(object2.toString()));
		}
		JSONArray array = bind().dirty().page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) bind().count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public int sort(String tempid, long num) {
		JSONObject object = new JSONObject();
		object.put("sort", num);
		return bind().eq("_id", new ObjectId(tempid)).data(object)
				.update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public int setTid(String tempid, String tid) {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		return bind().eq("_id", new ObjectId(tempid)).data(object)
				.update() != null ? 0 : 99;
	}

	public int delete(String[] arr) {
		bind().or();
		for (int i = 0; i < arr.length; i++) {
			bind().eq("_id", new ObjectId(arr[i]));
		}
		return bind().deleteAll() == arr.length ? 0 : 99;
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
	private String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
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
