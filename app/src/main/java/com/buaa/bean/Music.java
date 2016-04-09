package com.buaa.bean;

/**
 * Created by Administrator on 2016/3/19.
 */
public class Music {
    private long id;
    private String name;
    private String author;
        public Music(String name, String author) {
            this.name = name;
            this.author=author;
        }

    public String getAuthor(){
            return author;
        }
        public String getName() {
            return name;
        }
        public long getId() {
        return id;
        }
}
