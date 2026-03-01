package com.restaurant.pos.orderservice.order.repository

import com.restaurant.pos.orderservice.order.OrderRepository
import com.restaurant.pos.orderservice.order.entity.Order
import com.restaurant.pos.orderservice.order.entity.OrderItem
import com.restaurant.pos.orderservice.order.dto.CustomerDto
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class OrderRepositoryTest extends Specification {

	@Subject
	OrderRepository orderRepository = Mock(OrderRepository)

	def "should save a new Order successfully"() {
		given:
		def item = new Order(
			customer: new CustomerDto(fullName: "John Doe", address: "123 St", email: "john@example.com"),
			orderItems: [new OrderItem(productId: "p1", name: "Item 1", quantity: 2, price:  BigDecimal.valueOf(10.0))],
			totalAmount: BigDecimal.valueOf(20.0),
			createdAt: LocalDateTime.now(),
			updatedAt: LocalDateTime.now()
		)
		def savedItem = new Order(
			id: "order123",
			customer: item.customer,
			orderItems: item.orderItems,
			totalAmount: item.totalAmount,
			createdAt: item.createdAt,
			updatedAt: item.updatedAt
		)
		orderRepository.save(item) >> savedItem

		when:
		def result = orderRepository.save(item)

		then:
		result.id == "order123"
		result.totalAmount == BigDecimal.valueOf(20.0)
	}

	def "should find Order by ID"() {
		given:
		def item = new Order(id: "o123", totalAmount: BigDecimal.valueOf(45.50))
		orderRepository.findById("o123") >> Optional.of(item)

		when:
		def result = orderRepository.findById("o123")

		then:
		result.isPresent()
		result.get().totalAmount == BigDecimal.valueOf(45.50)
	}

	def "should return empty Optional when ID not found"() {
		given:
		orderRepository.findById("nonexistent") >> Optional.empty()

		when:
		def result = orderRepository.findById("nonexistent")

		then:
		result.isEmpty()
	}

	def "should delete Order by ID"() {
		when:
		orderRepository.deleteById("order123")

		then:
		noExceptionThrown()
	}

	def "should check if Order exists"() {
		given:
		orderRepository.existsById("order123") >> true

		when:
		def exists = orderRepository.existsById("order123")

		then:
		exists == true
	}

	def "should return false when Order does not exist"() {
		given:
		orderRepository.existsById("fake123") >> false

		when:
		def exists = orderRepository.existsById("fake123")

		then:
		exists == false
	}

	def "should find paginated orders"() {
		given:
		def o1 = new Order(id: "a", totalAmount: BigDecimal.valueOf(10.0))
		def o2 = new Order(id: "b", totalAmount: BigDecimal.valueOf(20.0))
		def page = new PageImpl<Order>([o1, o2], PageRequest.of(0, 2), 2)
		orderRepository.findAll(_ as PageRequest) >> page

		when:
		def result = orderRepository.findAll(PageRequest.of(0, 2))

		then:
		result.content.size() == 2
		result.totalElements == 2
	}

	def "should delete all orders"() {
		when:
		orderRepository.deleteAll()

		then:
		noExceptionThrown()
	}
}

