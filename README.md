

```console
docker run --name mongodb -d -p 27017:27017 mongodb/mongodb-community-server:4.4.22-ubi8
docker run --name elasticsearch -d -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" docker.elastic.co/elasticsearch/elasticsearch:8.1.0
```