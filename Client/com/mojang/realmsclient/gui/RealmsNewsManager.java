/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.util.RealmsPersistence;

public class RealmsNewsManager {
    private final RealmsPersistence newsLocalStorage;
    private boolean hasUnreadNews;
    private String newsLink;

    public RealmsNewsManager(RealmsPersistence $$0) {
        this.newsLocalStorage = $$0;
        RealmsPersistence.RealmsPersistenceData $$1 = $$0.read();
        this.hasUnreadNews = $$1.hasUnreadNews;
        this.newsLink = $$1.newsLink;
    }

    public boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }

    public String newsLink() {
        return this.newsLink;
    }

    public void updateUnreadNews(RealmsNews $$0) {
        RealmsPersistence.RealmsPersistenceData $$1 = this.updateNewsStorage($$0);
        this.hasUnreadNews = $$1.hasUnreadNews;
        this.newsLink = $$1.newsLink;
    }

    private RealmsPersistence.RealmsPersistenceData updateNewsStorage(RealmsNews $$0) {
        boolean $$3;
        RealmsPersistence.RealmsPersistenceData $$1 = new RealmsPersistence.RealmsPersistenceData();
        $$1.newsLink = $$0.newsLink;
        RealmsPersistence.RealmsPersistenceData $$2 = this.newsLocalStorage.read();
        boolean bl = $$3 = $$1.newsLink == null || $$1.newsLink.equals((Object)$$2.newsLink);
        if ($$3) {
            return $$2;
        }
        $$1.hasUnreadNews = true;
        this.newsLocalStorage.save($$1);
        return $$1;
    }
}