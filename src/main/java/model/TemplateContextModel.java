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

//	public TemplateContextModel() {
//		_form = new formHelper();
//		_form.addNotNull("tid,name,time");
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("tempid", getID());
//		map.put("ownid", 0);
//		map.put("isdelete", 0);
//		map.put("sort", 0);
//		_form.adddef(map);
//	}

	static {
		dbtemp = new DBHelper("mongodb", "tempcontext", "_id");
		_form =dbtemp.getChecker();
	}

	public TemplateContextModel(){
		_form.putRule("tid"/*,name,time"*/, formdef.notNull);
	}
	public int insert(JSONObject tempinfo) {
		if (!_form.checkRuleEx(tempinfo)) {
			return 1;
		}
//		int cknode = _form.check_forminfo(tempinfo);
//		if (cknode == 1) {
//			return 1;
//		}
		return dbtemp.data(tempinfo).insertOnce() != null ? 0 : 99;
	}

	public int delete(String id) {
		return dbtemp.eq("_id", new ObjectId(id)).delete() != null ? 0 : 99;
		// return dbtemp.delete(new ObjectId(id))==true ? 0 : 99;
	}

	public int update(String tempid,JSONObject tempInfo) {
		// 非空字段查询
		if (!_form.checkRule(tempInfo)) {
			return 1;
		}
		return dbtemp.eq("_id", new ObjectId(tempid)).data(tempInfo)
				.update() != null ? 0 : 99;
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

	public String page(int idx, int pageSize) {
		JSONArray array = dbtemp.page(idx, pageSize);
		@SuppressWarnings("unchecked")
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;

			{
				put("totalSize", (int) Math.ceil((double) dbtemp.count() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object.toString();
//		return dbtemp.page(idx, pageSize);
	}
	public JSONObject page(String tempinfo,int idx, int pageSize) {
		@SuppressWarnings("unchecked")
		Set<Object> set = JSONHelper.string2json(tempinfo).keySet();
		for (Object object2 : set) {
			dbtemp.eq(object2.toString(), JSONHelper.string2json(tempinfo).get(object2.toString()));
		}
		JSONArray array = dbtemp.page(idx, pageSize);
		@SuppressWarnings("unchecked")
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;
			{
				put("totalSize", (int) Math.ceil((double) dbtemp.count() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object;
//		return dbtemp.page(idx, pageSize);
	}

	@SuppressWarnings("unchecked")
	public int sort(String tempid,long num) {
		JSONObject object = new JSONObject();
		object.put("sort", num);
		return dbtemp.eq("_id", new ObjectId(tempid)).data(object).update()!=null?0:99;
	}

	@SuppressWarnings("unchecked")
	public int setTid(String tempid,String tid) {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		return dbtemp.eq("_id", new ObjectId(tempid)).data(object).update()!=null?0:99;
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
