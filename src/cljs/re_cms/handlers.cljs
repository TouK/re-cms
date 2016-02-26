(ns re-cms.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [re-cms.db :as db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn content-url [root]
  (str root "/content"))

(defn section-url [root page section]
  (str root "/content/" page "/" section))

(re-frame/register-handler
  :initialize-db
  (fn [_ [_ url]]
    (re-frame/dispatch [:sync-db url])
    (assoc db/default-db :base-uri url)))

(re-frame/register-handler
  :sync-db
  (fn [db [_ url]]
    (go
      (let [response (<! (http/get (content-url url)))]
        (re-frame/dispatch [:sync-db-response (:body response)])))
    db))

(re-frame/register-handler
  :sync-db-response
  (fn [db [_ data]]
    (assoc db :values data)))

(re-frame/register-handler
  :save
  (fn [db [_ {:keys [page section value] :as row}]]
    (go (http/post (section-url (:base-uri db) page section) {:json-params {:text value}}))
    (assoc db :values
              (->> (:values db)
                   (cons row)))))

(re-frame/register-handler
  :update
  (fn [db [_ {:keys [page section value] :as row}]]
    (go (http/put (section-url (:base-uri db) page section) {:json-params {:text value}}))
    (assoc db :values
              (->>
                (:values db)
                (filter #(not (and (= (:page %) page) (= (:section %) section))))
                (cons row)))))