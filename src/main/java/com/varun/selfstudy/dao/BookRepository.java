package com.varun.selfstudy.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.varun.selfstudy.vo.Book;

@Repository
public interface BookRepository extends CassandraRepository<Book, String> {

}
