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

public class TempListModel {
	private static DBHelper dbtemp;
	private static formHelper _form;
	static {
		dbtemp = new DBHelper("mongodb", "templist", "_id");
		_form = dbtemp.getChecker();
	}

	public TempListModel() {
		_form.putRule("name", formdef.notNull);
	}

	public int insert(JSONObject tempinfo) {
		if (!_form.checkRuleEx(tempinfo)) {
			return 1;
		}
//		int ckcode = _form.checkRuleEx(tempinfo);
//		if (ckcode == 1) {
//			return 1;
//		}
		return dbtemp.insert(tempinfo) != null ? 0 : 99;
	}

	public int delete(String id) {
		return dbtemp.eq("_id", new ObjectId(id)).delete()!=null?0:99;
	}

	public JSONArray select() {
		return dbtemp.select();
	}

	public JSONArray select(String tempinfo) {
		JSONObject object = JSONHelper.string2json(tempinfo);
		@SuppressWarnings("unchecked")
		Set<Object> set = object.keySet();
		for (Object object2 : set) {
			dbtemp.eq(object2.toString(), object.get(object2.toString()));
		}
		return dbtemp.select();
	}

	public int update(String tid, JSONObject tempinfo) {
		// 非空字段判断
		if (!_form.checkRuleEx(tempinfo)) {
			return 1;
		}
		return dbtemp.eq("_id", new ObjectId(tid)).data(tempinfo).update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONArray array = dbtemp.page(idx, pageSize);
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;

			{
				put("totalSize", (int) Math.ceil((double) array.size() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object.toString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(String tempinfo, int idx, int pageSize) {
		Set<Object> set = JSONHelper.string2json(tempinfo).keySet();
		for (Object object2 : set) {
			dbtemp.eq(object2.toString(), JSONHelper.string2json(tempinfo).get(object2.toString()));
		}
		JSONArray array = dbtemp.page(idx, pageSize);
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;

			{
				put("totalSize", (int) Math.ceil((double) array.size() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object;
	}

	@SuppressWarnings("unchecked")
	public int sort(String tid, long num) {
		JSONObject object = new JSONObject();
		object.put("sort", num);
		return dbtemp.eq("_id", new ObjectId(tid)).data(object).update()!=null?0:99;
	}

	public int delete(String[] arr) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			int code = delete(arr[i]);
			if (code != 0) {
				stringBuffer.append((i + 1) + ",");
			}
		}
		return stringBuffer.length() == 0 ? 0 : 3;
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
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map,JSONObject object) {
		if (map.entrySet()!=null) {
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
