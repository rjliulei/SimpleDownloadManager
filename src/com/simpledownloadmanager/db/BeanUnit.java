package com.simpledownloadmanager.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * 实体类的操作单元
 * 
 * @author liulei
 * 
 */
public abstract class BeanUnit<T> {

	protected DataHelper dataHelper = null;
	protected Dao<T, Integer> dao = null;
	protected Context context;
	private Class<T> paramClass;

	public BeanUnit(Context context) {

		this.context = context;
		dataHelper = OpenHelperManager.getHelper(context, DataHelper.class);
		paramClass = (Class<T>)getSuperClassGenricType(getClass(), 0);
		try {

			dao = dataHelper.getMyDao(paramClass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     * 
     *@param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     *         determined
     */
    @SuppressWarnings("unchecked")
    private static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {
    	
    	//返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();
        

    	clazz.getTypeParameters();

        if (!(genType instanceof ParameterizedType)) {
        	//本类没有继承泛型超类
        	
           return Object.class;
        }
        
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
                     return Object.class;
        }
        if (!(params[index] instanceof Class)) {
              return Object.class;
        }

        return (Class) params[index];
    }

	public void createItem(T obj) {
		try {
			dao.create(obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateItem(T obj) {
		try {
			dao.update(obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 通过主键id查询单条记录
	 * @author liulei	
	 * @date 2015-4-4
	 * @param id
	 * @return T   
	*/
	public T queryItem(int id){
		
		try {
			T item = dao.queryForId(id);
			return item;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public List<T> queryAllItems() {
		List<T> items = null;
		try {
			items = dao.queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	public void deleteItem(T obj) {
		try {
			dao.delete(obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearTable() {
		dao.clearObjectCache();
	}
	
	public void clear(){
		DeleteBuilder< T, Integer> deleteBuilder = dao.deleteBuilder();
		try {
			deleteBuilder.delete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearAndAdd(List<T> newList) {
		List<T> items = queryAllItems();
		try {
			dao.delete(items);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < newList.size(); i++) {
			createItem(newList.get(i));
		}

	}
	
}
