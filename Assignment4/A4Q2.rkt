#lang racket
(require math/number-theory)

;; a list of the first 1000 integers
(define first-1000-nums (build-list 1000 values))
;; a list of all primes < 354
(define prime-list (filter prime? (build-list 354 values)))
(define q 24591437642601686069585809261845683826072619627520957132835061935152051389061)

;; Calculate large exponents the fast way
(define (exponent-mod g a p)
  (cond
    [(equal? 0 a) 1]
    [(even? a) (remainder (expt (exponent-mod g (/ a 2) p) 2) p)]
    [else (* (remainder g p) (exponent-mod g (- a 1) p))]))

(define p (+ 1 (* q (foldr * 1 prime-list))))
(define g (exponent-mod 26 (/ (- p 1) q) p))

;; Given g^x and a prime factor l, compute x mod l
(define (find-x-mod-l l f-of-l fl-no-x numbers-to-test)
  (cond
    [(empty? numbers-to-test) (printf "FAILURE")]
    [(equal? (exponent-mod fl-no-x (first numbers-to-test) p) f-of-l) (printf "~a mod ~a\n" (first numbers-to-test) l)]
    [else (find-x-mod-l l f-of-l fl-no-x (rest numbers-to-test))]))

;; Read the list of pairs of l and f(l) from the input file
(define (read-input file)
  (define line (read-line file))
  (cond
    [(eof-object? line) empty]
    [else (cons (map string->number (string-split line)) (read-input file))]))
(define l-fl-pairs (read-input (open-input-file "a4-q2.txt")))

;; Iterate through each pair of l and f(l) to print x mod l
(for-each (lambda (pair)
            (define l (first pair))
            (define fl (second pair))
            (find-x-mod-l l fl (exponent-mod 26 (/ (- p 1) l) p) first-1000-nums))
          l-fl-pairs)

;; run the above code to print out the values of x mod l for each l/f(l) pair, then
;; define this list "x-list" and run "(solve-chinese x-list prime-list)" to get the x value
(define x-list '(0 0 0 12 1 4 16 14 26 16 38 36 66 60 50 62 78 68 26 136 98 144 136 142 48 98 124 148 120 86 232 236 46 52 212
                   142 308 122 270 338 192 212 162 246 392 232 362 304 30 270 260 192 14 118 400 134 432 500 454 62
                   190 408 266 400 8 612 194 588 470 230 208))
(solve-chinese x-list prime-list)