describe('dashboard.factories', function() {
	beforeEach(module('dashboard.factories'));

	describe('AbvCalculator', function() {
		
	 	it("should be tested at some stage", inject(function (AbvCalculator) {

	 	}));

	});

	describe('IbuCalculator', function() {

		it("should calculate correctly for a 1.060 gravity and normal boil", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.060, 60, 6.4, 15, 22)).toBeCloseTo(9.20, 2);
		}));

		it("should calculate correctly for a 1.070 gravity and normal boil", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.070, 60, 6.4, 15, 22)).toBeCloseTo(8.41, 2);
		}));

		it("should calculate correctly for a 1.035 gravity and normal boil", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.035, 60, 6.4, 15, 22)).toBeCloseTo(11.52, 2);
		}));

		it("should calculate correctly for a short boil", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.060, 3, 6.4, 15, 22)).toBeCloseTo(1.14, 2);
		}));

		it("should calculate correctly for a normal boil with a high alpha acid hop", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.060, 60, 12.4, 15, 22)).toBeCloseTo(17.83, 2);
		}));

		it("should calculate correctly for a normal boil with a large quantity", inject(function (IbuCalculator) {
			expect(IbuCalculator.calculate(1.060, 60, 6.4, 50, 22)).toBeCloseTo(30.67, 2);
		}));

		//TODO 
		// it("should calculate correctly for a dry hopping", inject(function (IbuCalculator) {
		// 	expect(IbuCalculator.calculate(1.070, 60, 6.4, 15, 22)).toBeCloseTo(0, 2);
		// }));

		// it("should calculate correctly for a secondary hopping", inject(function (IbuCalculator) {
		// 	expect(IbuCalculator.calculate(1.060, 60, 6.4, 15, 22)).toBeCloseTo(0, 2);
		// }));
	});
});
