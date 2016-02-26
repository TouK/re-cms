(ns re-cms.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [re-cms.db :as db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def api-root "http://localhost:8080")

(def content-url (str api-root "/content"))

(defn section-url [page section]
  (str api-root "/content/" page "/" section))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    (re-frame/dispatch [:sync-db])
     db/default-db))

(re-frame/register-handler
  :sync-db
  (fn [db _]
    (go
      (let [response (<! (http/get content-url))]
        (re-frame/dispatch [:sync-db-response (:body response)])))
    db))

(re-frame/register-handler
  :sync-db-response
  (fn [db [_ data]]
    (assoc db :values data)))

(re-frame/register-handler
  :save
  (fn [db [_ {:keys [page section value] :as row}]]
    (go (http/post (section-url page section) {:json-params {:text value}}))
    (assoc db :values
              (->> (:values db)
                   (cons row)))))

(re-frame/register-handler
  :update
  (fn [db [_ {:keys [page section value] :as row}]]
    (go (http/put (section-url page section) {:json-params {:text value}}))
    (assoc db :values
              (->>
                (:values db)
                (filter #(not (and (= (:page %) page) (= (:section %) section))))
                (cons row)))))