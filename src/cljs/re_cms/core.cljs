(ns re-cms.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-cms.handlers]
              [re-cms.subs]
              [re-cms.views :as views]
              [re-cms.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/app]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
