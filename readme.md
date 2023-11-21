```shell
java -jar target/report-merging-jar-with-dependencies.jar backup -in {cucumber.json file} -out {backup directory}
```

```shell
java -jar target/report-merging-jar-with-dependencies.jar merge \
  --source {merged version file} \
  --target {bakcup directory} \
  --bk-dir {final backup directory}
```
