FROM clojure:openjdk-8-lein-buster
COPY src/ src/
COPY project.clj project.clj
COPY .nrepl-port .nrepl-port
RUN lein with-profile bindings deps
ENTRYPOINT ["lein", "with-profile", "bindings", "trampoline", "repl", ":headless", ":host", "0.0.0.0", ":port", "40000"]