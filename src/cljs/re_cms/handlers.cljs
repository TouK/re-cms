(ns re-cms.handlers
    (:require [re-frame.core :as re-frame]
              [re-cms.db :as db]))

(re-frame/register-handler
  :initialize-db
  (fn  [_ _]
     db/default-db))

(re-frame/register-handler
  :save
  (fn [db [_ {:keys [page section value] :as row}]]
    (conj db row)))

(re-frame/register-handler
  :update
  (fn [db [_ {:keys [page section value] :as row}]]
    (->
      (filter #(not (and (= (:page %) page) (= (:section %) section))) db)
      (conj row))))