package com.cos.phoneapp.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Phone {
	@Id //pk설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)//Table,auto_increment,Sequence
	private Long id;
	
	private String name;
	private String tel;
}
