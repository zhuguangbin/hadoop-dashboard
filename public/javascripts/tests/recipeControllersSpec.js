describe('dashboard.controllers.recipe', function() {

	beforeEach(module('dashboard.controllers.recipe'));
	
	describe('RecipeListCtrl', function() {

		var location;

		function GridEvent(obj) {

			this.items = [];
			this.items[0] = 0;
			this.item = obj;
			this.select = select;
			this.dataItem = dataItem;

			function select() {
				return this.items;
			}

			function dataItem(index) {
				return obj;
			}
		} 

		beforeEach( inject( function( $rootScope, $controller, $location) {
			location = $location;
			$scope = $rootScope.$new();
			ctrl = $controller('RecipeListCtrl', { 
				$scope: $scope,
				$location: $location 
			});
		}));

		
	 	it("should change path to create a new recipe", inject(function () {
	 		location.path('/recipes');

	 		$scope.createNewRecipe();

	 		expect(location.path()).toEqual('/recipes/new');
	 	}));

	 	it("should change path to the selected recipe", inject(function () {
	 		location.path('/recipes');

			var mockedEvent = {sender: new GridEvent( { id: 8}) };
	 		$scope.onSelection(mockedEvent);

	 		expect(location.path()).toEqual('/recipes/8');
	 	}));

	});

	describe('RecipeController', function() {
		beforeEach( inject( function( $rootScope, $controller, $location) {
			location = $location;
			$scope = $rootScope.$new();
			ctrl = $controller('RecipeController', { 
				$scope: $scope,
				$location: $location 
			});
		}));
	});
	
});
