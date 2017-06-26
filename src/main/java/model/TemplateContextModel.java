package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import apps.appsProxy;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;
import esayhelper.CacheHelper;
import esayhelper.JSONHelper;
import esayhelper.jGrapeFW_Message;
import nlogger.nlogger;

public class TemplateContextModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();

	static {
		dbtemp = new DBHelper(appsProxy.configValue().get("db").toString(), "templateContect");
		_form = dbtemp.getChecker();
	}

	private db bind() {
		return dbtemp.bind(String.valueOf(appsProxy.appid()));
	}

	public TemplateContextModel() {
		_form.putRule("name", formdef.notNull);
		_form.putRule("time", formdef.notNull);
	}

	public int insert(JSONObject tempinfo) {
		int code = 99;
		if (tempinfo != null) {
			try {
				if (!_form.checkRuleEx(tempinfo)) {
					return 1;
				}
				Object obj = bind().data(tempinfo).insertOnce();
				code = (obj != null ? 0 : 99);
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

	public int update(String tempid, JSONObject tempInfo) {
		int code = 99;
		JSONObject object = null;
		try {
			if (tempInfo != null) {
				try {
					object = new JSONObject();
					object = bind().eq("_id", new ObjectId(tempid)).data(tempInfo).update();
					code = (object != null ? 0 : 99);
				} catch (Exception e) {
					nlogger.logout(e);
					code = 99;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return code;
	}

	public JSONObject find(String tid) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			object = bind().eq("_id", new ObjectId(tid)).find();
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return object != null ? object : null;
	}

	public String select(String tempinfo) {
		JSONArray array = null;
		JSONObject object = JSONHelper.string2json(tempinfo);
		if (object != null) {
			try {
				array = new JSONArray();
				for (Object object2 : object.keySet()) {
					if ("_id".equals(object2.toString())) {
						bind().eq("_id", new ObjectId(object.get("_id").toString()));
					}
					bind().eq(object2.toString(), object.get(object2.toString()));
				}
				array = bind().limit(20).select();
			} catch (Exception e) {
				nlogger.logout(e);
				array = null;
			}
		}
		return resultMessage(array);
	}

	// 根据模版类型显示模版
	public String search(String type) {
		JSONArray array = null;
		try {
			array = new JSONArray();
			array = bind().eq("type", type).limit(20).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = bind().page(idx, pageSize);
			object = new JSONObject();
			object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(String tempinfo, int idx, int pageSize) {
		JSONObject object = null;
		JSONObject Info = JSONHelper.string2json(tempinfo);
		if (Info != null) {
			try {
				for (Object object2 : Info.keySet()) {
					if ("_id".equals(object2.toString())) {
						bind().eq("_id", new ObjectId(Info.get("_id").toString()));
					}
					bind().eq(object2.toString(), Info.get(object2.toString()));
				}
				object = new JSONObject();
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
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public int sort(String tempid, long num) {
		int code = 99;
		JSONObject object = new JSONObject();
		object.put("sort", num);
		if (object!=null) {
			try {
				JSONObject obj = bind().eq("_id", new ObjectId(tempid)).data(object).update();
				code = (obj!= null ? 0 : 99);
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return code;
	}

	@SuppressWarnings("unchecked")
	public int setTid(String tempid, String tid) {
		int code = 99;
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		if (object!=null) {
			try {
				JSONObject obj = bind().eq("_id", new ObjectId(tempid)).data(object).update();
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
	private String resultMessage(JSONObject object) {
		if (object==null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
		if (array==null) {
			array = new JSONArray();
		}
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
