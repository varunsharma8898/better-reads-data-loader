package com.varun.selfstudy.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.varun.selfstudy.vo.Author;

@Repository
public interface AuthorRepository extends CassandraRepository<Author, String> {

}
