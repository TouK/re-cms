(ns re-cms.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-cms.handlers]
              [re-cms.subs]
              [re-cms.views :as views]
              [re-cms.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root [elem]
  (reagent/render [views/app] elem))

(defn ^:export init [elem]
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root elem))
