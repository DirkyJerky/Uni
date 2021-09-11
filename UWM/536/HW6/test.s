			#Begin Fn
			#  Begin Preamble
	.text
fn1:
			#  End Preamble
			#  Begin Prologue
	sw    $ra, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	sw    $fp, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	subu  $sp, $sp, 0		#locals space
	addu  $fp, $sp, 8		#update fp
			#  End Prologue
			#  Begin Body
			#begin return (x(int)[o=4] != 3);

			#begin (x(int)[o=4] != 3)
			#begin x(int)[o=4] (rhs)
	lw    $t0, 4($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end x(int)[o=4] (rhs)
			#begin 3
	li    $t0, 3
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end 3
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $t2, 0
	beq   $t0, $t1, .L0
	addu  $t2, 1
.L0:
	sw    $t2, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end (x(int)[o=4] != 3)
	lw    $v0, 4($sp)	#POP
	addu  $sp, $sp, 4
	la    $t0, fn1_exit
	jr    $t0
			#end return (x(int)[o=4] != 3);

			#  End Body
			#  Begin Epilogue
fn1_exit:
	lw    $ra, 0($fp)
	move  $t0, $fp
	lw    $fp, -4($fp)
	move  $sp, $t0
	jr    $ra
			#  End Epilogue
			#End Fn
			#Begin Fn
			#  Begin Preamble
	.text
main:
			#  End Preamble
			#  Begin Prologue
	sw    $ra, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	sw    $fp, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	subu  $sp, $sp, 8		#locals space
	addu  $fp, $sp, 16		#update fp
			#  End Prologue
			#  Begin Body
			#cin >> x(int)[o=-8];

	li    $v0, 5
	syscall
			#begin x(int)[o=-8] (lhs)
	la    $t0, -8($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end x(int)[o=-8] (lhs)
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	sw    $v0, 0($t0)
			#end cin >> x(int)[o=-8];

			#begin cout << x(int)[o=-8];

			#begin x(int)[o=-8] (rhs)
	lw    $t0, -8($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end x(int)[o=-8] (rhs)
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << x(int)[o=-8];

			#begin cout << ("a" == "abc");

			#begin ("a" == "abc")
			#begin "a"
	.data
.L3:	.asciiz "a"
	.text
	la    $t0, .L3
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "a"
			#begin "abc"
	.data
.L4:	.asciiz "abc"
	.text
	la    $t0, .L4
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "abc"
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L1:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L2
	beq   $t2, $zero, .L2
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L1
.L2:
	li    $t4, 0
	bne   $t2, $zero, .L5
	addi  $t4, $t4, 1
.L5:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("a" == "abc")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("a" == "abc");

			#begin cout << ("a" == "A");

			#begin ("a" == "A")
			#begin "a"
	.data
.L8:	.asciiz "a"
	.text
	la    $t0, .L8
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "a"
			#begin "A"
	.data
.L9:	.asciiz "A"
	.text
	la    $t0, .L9
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "A"
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L6:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L7
	beq   $t2, $zero, .L7
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L6
.L7:
	li    $t4, 0
	bne   $t2, $zero, .L10
	addi  $t4, $t4, 1
.L10:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("a" == "A")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("a" == "A");

			#begin cout << ("a" == "a");

			#begin ("a" == "a")
			#begin "a"
	.data
.L13:	.asciiz "a"
	.text
	la    $t0, .L13
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "a"
			#begin "a"
	.data
.L14:	.asciiz "a"
	.text
	la    $t0, .L14
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "a"
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L11:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L12
	beq   $t2, $zero, .L12
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L11
.L12:
	li    $t4, 0
	bne   $t2, $zero, .L15
	addi  $t4, $t4, 1
.L15:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("a" == "a")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("a" == "a");

			#begin cout << ("abc" == "abc");

			#begin ("abc" == "abc")
			#begin "abc"
	.data
.L18:	.asciiz "abc"
	.text
	la    $t0, .L18
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "abc"
			#begin "abc"
	.data
.L19:	.asciiz "abc"
	.text
	la    $t0, .L19
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "abc"
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L16:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L17
	beq   $t2, $zero, .L17
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L16
.L17:
	li    $t4, 0
	bne   $t2, $zero, .L20
	addi  $t4, $t4, 1
.L20:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("abc" == "abc")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("abc" == "abc");

			#begin cout << ("" == "abc");

			#begin ("" == "abc")
			#begin ""
	.data
.L23:	.asciiz ""
	.text
	la    $t0, .L23
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ""
			#begin "abc"
	.data
.L24:	.asciiz "abc"
	.text
	la    $t0, .L24
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "abc"
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L21:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L22
	beq   $t2, $zero, .L22
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L21
.L22:
	li    $t4, 0
	bne   $t2, $zero, .L25
	addi  $t4, $t4, 1
.L25:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("" == "abc")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("" == "abc");

			#begin cout << ("" == "");

			#begin ("" == "")
			#begin ""
	.data
.L28:	.asciiz ""
	.text
	la    $t0, .L28
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ""
			#begin ""
	.data
.L29:	.asciiz ""
	.text
	la    $t0, .L29
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ""
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
.L26:
	lb    $t2, 0($t0)
	lb    $t3, 0($t1)
	bne   $t2, $t3, .L27
	beq   $t2, $zero, .L27
	addi  $t0, $t0, 1
	addi  $t1, $t1, 1
	j     .L26
.L27:
	li    $t4, 0
	bne   $t2, $zero, .L30
	addi  $t4, $t4, 1
.L30:
	sw    $t4, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end ("" == "")
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << ("" == "");

			#if (fn1(int->bool)(x(int)[o=-8])) {
			#begin fn1(int->bool)(x(int)[o=-8])
			#begin x(int)[o=-8]
			#begin push #0
			#begin x(int)[o=-8] (rhs)
	lw    $t0, -8($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end x(int)[o=-8] (rhs)
			#end push #0
			#end x(int)[o=-8]
	jal   fn1
	addu  $sp, $sp, 4		#tear down params
	sw    $v0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end fn1(int->bool)(x(int)[o=-8])
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	beq   $t0, $zero, .L31_else
			#begin cout << "Neq 3";

			#begin "Neq 3"
	.data
.L33:	.asciiz "Neq 3"
	.text
	la    $t0, .L33
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "Neq 3"
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 4
	syscall
			#end cout << "Neq 3";

	j     .L32_exit
			#} else {
.L31_else:
			#begin cout << "Eq 3";

			#begin "Eq 3"
	.data
.L34:	.asciiz "Eq 3"
	.text
	la    $t0, .L34
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "Eq 3"
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 4
	syscall
			#end cout << "Eq 3";

.L32_exit:
			#} end ifelse
			#begin cout << "\n";

			#begin "\n"
	.data
.L35:	.asciiz "\n"
	.text
	la    $t0, .L35
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "\n"
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 4
	syscall
			#end cout << "\n";

			#y(int)[o=-12] = x(int)[o=-8];

			#begin (y(int)[o=-12] = x(int)[o=-8])
			#begin x(int)[o=-8] (rhs)
	lw    $t0, -8($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end x(int)[o=-8] (rhs)
			#begin y(int)[o=-12] (lhs)
	la    $t0, -12($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end y(int)[o=-12] (lhs)
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	sw    $t1, 0($t0)
	sw    $t1, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end (y(int)[o=-12] = x(int)[o=-8])
	addu  $sp, $sp, 4
			#end y(int)[o=-12] = x(int)[o=-8];

			#while ((y(int)[o=-12] != 0)) {
.L36_start:
			#begin (y(int)[o=-12] != 0)
			#begin y(int)[o=-12] (rhs)
	lw    $t0, -12($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end y(int)[o=-12] (rhs)
			#begin 0
	li    $t0, 0
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end 0
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $t2, 0
	beq   $t0, $t1, .L38
	addu  $t2, 1
.L38:
	sw    $t2, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end (y(int)[o=-12] != 0)
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	beq   $t0, $zero, .L37_exit
			#begin cout << y(int)[o=-12];

			#begin y(int)[o=-12] (rhs)
	lw    $t0, -12($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end y(int)[o=-12] (rhs)
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
			#end cout << y(int)[o=-12];

			#begin cout << "\n";

			#begin "\n"
	.data
.L39:	.asciiz "\n"
	.text
	la    $t0, .L39
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end "\n"
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 4
	syscall
			#end cout << "\n";

			#y(int)[o=-12]--;

			#begin y(int)[o=-12] (lhs)
	la    $t0, -12($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end y(int)[o=-12] (lhs)
			#begin y(int)[o=-12] (rhs)
	lw    $t0, -12($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
			#end y(int)[o=-12] (rhs)
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	addi  $t0, $t0, -1
	sw    $t0, 0($t1)
			#end y(int)[o=-12]--;

	j     .L36_start
.L37_exit:
			#} end while
			#  End Body
			#  Begin Epilogue
main_exit:
	lw    $ra, 0($fp)
	move  $t0, $fp
	lw    $fp, -4($fp)
	move  $sp, $t0
	li    $v0, 10
	syscall
			#  End Epilogue
			#End Fn
