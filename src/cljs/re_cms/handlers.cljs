(ns re-cms.handlers
    (:require [re-frame.core :as re-frame]
              [re-cms.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))
