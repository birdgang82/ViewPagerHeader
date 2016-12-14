/*
 * Copyright 2015 chenupt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viewpagerheader.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEntity<T> implements Serializable {

    private long id;
    private T content;
    private boolean isCheck;
    private int status;
    private String modelType;
    private Class<?> modelView;
    private long timestamp;
    private boolean isSingleton;

    private String tag = "";

    private Map<String, Object> attrs;

    public ItemEntity() {
        this(null);
    }

    public ItemEntity(T t) {
        this.content = t;
        setTimestamp(System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public ItemEntity setId(long id) {
        this.id = id;
        return this;
    }

    public T getContent() {
        return content;
    }

    public T getContent(Class<T> c) {
        return (T)content;
    }

    public ItemEntity setContent(T content) {
        this.content = content;
        return this;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public ItemEntity setCheck(boolean isCheck) {
        this.isCheck = isCheck;
        return this;
    }

    public ItemEntity setStatus(int status){
        this.status = status;
        return this;
    }

    public int getStatus(){
        return this.status;
    }

    public String getModelType() {
        return modelType;
    }

    public ItemEntity setModelType(String modelType) {
        this.modelType = modelType;
        return this;
    }

    public Class<?> getModelView() {
        return modelView;
    }

    public ItemEntity setModelView(Class<?> modelView) {
        if(modelType == null){
            setModelType(modelView.getName());
        }
        this.modelView = modelView;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ItemEntity setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public ItemEntity setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public ItemEntity setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public ItemEntity<T> setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
        return this;
    }

    public ItemEntity<T> addAttr(String key, Object value) {
        if (attrs == null) {
            attrs = new HashMap<String, Object>();
        }
        attrs.put(key, value);
        return this;
    }

    public <T> T  getAttr(String key, Class<T> c) {
        if(attrs == null) {
            return null;
        }
        return (T) attrs.get(key);
    }

    public boolean hasAttr(String key) {
        if (attrs == null) {
            return false;
        }
        if (attrs.get(key) == null) {
            return false;
        }
        return true;
    }

    public void removeAttr(String key) {
        if (attrs.get(key) != null) {
            attrs.remove(key);
        }
    }

    public void attach(List<ItemEntity> list){
        list.add(this);
    }

}
