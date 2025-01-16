package telran.probes.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProbeDataRepo extends MongoRepository<ProbeDataDoc, ObjectId>{}
