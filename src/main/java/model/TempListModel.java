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
import nlogger.nlogger;

public class TempListModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();

	static {
		dbtemp = new DBHelper(appsProxy.configValue().get("db").toString(), "templateList", "_id");
		_form = dbtemp.getChecker();
	}

	private db bind() {
		return dbtemp.bind(String.valueOf(appsProxy.appid()));
	}

	public TempListModel() {
		_form.putRule("name", formdef.notNull);
	}

	public int insert(JSONObject tempinfo) {
		int code = 99;
		if (tempinfo != null) {
			try {
				if (!_form.checkRuleEx(tempinfo)) {
					return 1;
				}
				Object object = bind().data(tempinfo).insertOnce();
				code = (object != null ? 0 : 99);
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return code;
	}

	public int delete(String id) {
		int code = 99;
		JSONObject object = null;
		try {
			object = new JSONObject();
			object = bind().eq("_id", new ObjectId(id)).delete();
			code = (object != null ? 0 : 99);
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return code;
	}

	public String select() {
		JSONArray array = null;
		try {
			array = new JSONArray();
			array = bind().limit(20).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultmessage(array);
	}

	public String select(String tempinfo) {
		JSONArray array = null;
		JSONObject object = JSONHelper.string2json(tempinfo);
		if (object!=null) {
			try {
				array = new JSONArray();
				for (Object object2 : object.keySet()) {
					bind().eq(object2.toString(), object.get(object2.toString()));
				}
				array = bind().limit(20).select();
			} catch (Exception e) {
				nlogger.logout(e);
				array = null;
			}
		}
		return resultmessage(array);
	}

	public int update(String tid, JSONObject tempinfo) {
		int code = 99;
		JSONObject object = null;
		if (tempinfo != null) {
			try {
				object = new JSONObject();
				object = bind().eq("_id", new ObjectId(tid)).data(tempinfo).update();
				code = (object != null ? 0 : 99);
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return code;
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = bind().page(idx, pageSize);
			object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultmessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(String tempinfo, int idx, int pageSize) {
		JSONObject object = null;
		JSONObject info = JSONHelper.string2json(tempinfo);
		if (info!=null) {
			try {
				for (Object object2 : info.keySet()) {
					if ("_id".equals(object2.toString())) {
						bind().eq("_id", new ObjectId(info.get("_id").toString()));
					}
					bind().eq(object2.toString(), info.get(object2.toString()));
				}
				JSONArray array = bind().dirty().page(idx, pageSize);
				object = new JSONObject();
				object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
				object.put("currentPage", idx);
				object.put("pageSize", pageSize);
				object.put("data", array);
			} catch (Exception e) {
				nlogger.logout(e);
				object = null;
			}
		}
		return resultmessage(object);
	}

	@SuppressWarnings("unchecked")
	public int sort(String tid, long num) {
		int code = 99;
		JSONObject object = new JSONObject();
		object.put("sort", num);
		if (object!=null) {
			try {
				JSONObject obj = bind().eq("_id", new ObjectId(tid)).data(object).update();
				code = (obj!= null ? 0 : 99);
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return code;
	}

	public int delete(String[] arr) {
		int code = 99;
		try {
			bind().or();
			for (int i = 0; i < arr.length; i++) {
				bind().eq("_id", new ObjectId(arr[i]));
			}
			long codes = bind().deleteAll();
			code = (Integer.parseInt(String.valueOf(codes))== arr.length ? 0 : 99);
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return  code;
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
		if (object != null) {
			if (map.entrySet() != null) {
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
					if (!object.containsKey(entry.getKey())) {
						object.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	private String resultmessage(JSONObject object) {
		if (object==null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultmessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultmessage(JSONArray array) {
		if (array==null) {
			array = new JSONArray();
		}
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
